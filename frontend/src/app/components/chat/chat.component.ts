import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import { InquiryService } from '../../services/inquiry.service';
import { HttpClient } from '@angular/common/http';

import { CommonModule, DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-chat',
  standalone: true,
  imports: [CommonModule, FormsModule, DatePipe],
  template: `
    <div class="card mt-4">
      <div class="card-body">
        <h3>Chat for Pet #{{ petId }}</h3>
        <div class="mb-3">
          <small class="text-muted">Connection: {{ connectionStatus }}</small>
          <span *ngIf="isTyping" class="ms-2 text-info">Someone is typing...</span>
        </div>

        <div class="chat-messages" style="height: 300px; overflow-y: auto; border: 1px solid #ddd; padding: 10px; margin-bottom: 10px;">
          <div *ngFor="let msg of messages" class="mb-2">
            <strong>{{ getSenderName(msg) }}:</strong> {{ msg.message }}
            <small class="text-muted">({{ msg.timestamp | date:'short' }})</small>
          </div>
        </div>

        <div class="input-group">
          <input [(ngModel)]="message"
                 (keyup)="onTyping()"
                 (keyup.enter)="sendMessage()"
                 class="form-control"
                 placeholder="Type a message..."
                 [disabled]="!isConnected" />
          <button class="btn btn-primary"
                  (click)="sendMessage()"
                  [disabled]="!isConnected || !message.trim()">
            Send
          </button>
        </div>

        <div class="mt-2">
          <button class="btn btn-sm btn-outline-secondary me-2" (click)="toggleConnection()">
            {{ isConnected ? 'Disconnect' : 'Connect' }}
          </button>
          <button class="btn btn-sm btn-outline-info" (click)="loadChatHistory()">
            Refresh
          </button>
        </div>
      </div>
    </div>
  `
})
export class ChatComponent implements OnInit, OnDestroy {
  petId!: string;
  chatId: string | null = null;
  messages: any[] = [];
  message: string = '';
  stompClient: Client | null = null;
  isConnected = false;
  connectionStatus = 'Disconnected';
  isTyping = false;
  typingTimer: any;
  pollingInterval: any;

  constructor(
    private route: ActivatedRoute,
    private inquiryService: InquiryService,
    private http: HttpClient
  ) {}

  ngOnInit() {
    this.route.params.subscribe(params => {
      this.petId = params['id'];
      this.initializeChat();
    });
  }

  ngOnDestroy() {
    this.disconnect();
    if (this.pollingInterval) {
      clearInterval(this.pollingInterval);
    }
  }

  initializeChat() {
    // Get user ID from localStorage or use test user ID 14
    const userId = localStorage.getItem('userId') || '14';
    const authToken = localStorage.getItem('authToken');

    console.log('Chat initialization debug:');
    console.log('userId from localStorage:', userId);
    console.log('authToken exists:', !!authToken);
    console.log('petId:', this.petId);

    // Try to start or find existing chat
    this.http.post<any>(`http://localhost:8080/api/chat/start?petId=${this.petId}&userId=${userId}`, {})
      .subscribe({
        next: (chat) => {
          this.chatId = chat.id;
          this.loadChatHistory();
          this.connectWebSocket();
          this.startPollingFallback();
        },
        error: (err) => {
          console.error('Failed to start chat:', err);
          console.error('Error details:', err.error);
          this.connectionStatus = 'Failed to start chat';
        }
      });
  }

  connectWebSocket() {
    try {
      const socket = new SockJS('http://localhost:8080/ws');
      this.stompClient = new Client({
        webSocketFactory: () => socket,
        reconnectDelay: 5000,
        heartbeatIncoming: 4000,
        heartbeatOutgoing: 4000
      });

      this.stompClient.onConnect = (frame) => {
        this.isConnected = true;
        this.connectionStatus = 'Connected (WebSocket)';
        console.log('Connected to WebSocket:', frame);

        // Subscribe to chat messages
        if (this.chatId) {
          this.stompClient?.subscribe(`/topic/chat/${this.chatId}`, (message) => {
            const inquiry = JSON.parse(message.body);
            this.messages.push(inquiry);
            this.scrollToBottom();
          });

          // Subscribe to typing indicators
          this.stompClient?.subscribe(`/topic/chat/${this.chatId}/typing`, (message) => {
            const typingData = JSON.parse(message.body);
            this.isTyping = typingData.typing;
          });

          // Subscribe to read receipts
          this.stompClient?.subscribe(`/topic/chat/${this.chatId}/read`, (message) => {
            console.log('Read receipt:', message.body);
          });

          // Join the chat
          this.stompClient?.publish({
            destination: `/app/chat/${this.chatId}/join`,
            body: JSON.stringify({ username: 'User' })
          });
        }
      };

      this.stompClient.onStompError = (frame) => {
        console.error('STOMP error:', frame);
        this.connectionStatus = 'WebSocket Error - Using Polling';
        this.isConnected = false;
      };

      this.stompClient.onWebSocketClose = () => {
        console.log('WebSocket closed');
        this.connectionStatus = 'WebSocket Closed - Using Polling';
        this.isConnected = false;
      };

      this.stompClient.activate();
    } catch (error) {
      console.error('WebSocket connection failed:', error);
      this.connectionStatus = 'WebSocket Failed - Using Polling Only';
    }
  }

  startPollingFallback() {
    // Poll for new messages every 3 seconds as fallback
    this.pollingInterval = setInterval(() => {
      if (this.chatId && !this.isConnected) {
        this.pollForNewMessages();
      }
    }, 3000);
  }

  pollForNewMessages() {
    if (!this.chatId) return;

    const lastMessageId = this.messages.length > 0 ? this.messages[this.messages.length - 1].id : 0;

    this.http.get<any[]>(`http://localhost:8080/api/chat/${this.chatId}/poll?lastMessageId=${lastMessageId}`)
      .subscribe({
        next: (newMessages) => {
          if (newMessages.length > 0) {
            this.messages.push(...newMessages);
            this.scrollToBottom();
          }
        },
        error: (err) => console.error('Polling error:', err)
      });
  }

  loadChatHistory() {
    if (!this.chatId) return;

    this.http.get<any[]>(`http://localhost:8080/api/chat/${this.chatId}/messages`)
      .subscribe({
        next: (messages) => {
          this.messages = messages.sort((a, b) =>
            new Date(a.timestamp).getTime() - new Date(b.timestamp).getTime()
          );
          this.scrollToBottom();
        },
        error: (err) => console.error('Failed to load chat history:', err)
      });
  }

  sendMessage() {
    if (!this.message.trim() || !this.chatId) return;

    // Get user ID from localStorage
    const userId = localStorage.getItem('userId') || '14';

    const messageData = {
      message: this.message.trim(),
      senderType: 'user',
      userId: userId
    };

    if (this.isConnected && this.stompClient) {
      // Send via WebSocket
      this.stompClient.publish({
        destination: `/app/chat/${this.chatId}/sendMessage`,
        body: JSON.stringify(messageData)
      });
    } else {
      // Send via HTTP polling
      this.http.post(`http://localhost:8080/api/chat/${this.chatId}/message`, null, {
        params: {
          message: messageData.message,
          senderType: messageData.senderType,
          userId: messageData.userId
        }
      }).subscribe({
        next: (inquiry) => {
          this.messages.push(inquiry);
          this.scrollToBottom();
        },
        error: (err) => console.error('Failed to send message:', err)
      });
    }

    this.message = '';
    this.stopTyping();
  }

  onTyping() {
    if (this.isConnected && this.stompClient && this.chatId) {
      // Send typing indicator via WebSocket
      this.stompClient.publish({
        destination: `/app/chat/${this.chatId}/typing`,
        body: JSON.stringify({ username: 'User', typing: true })
      });

      // Clear previous timer
      if (this.typingTimer) {
        clearTimeout(this.typingTimer);
      }

      // Stop typing after 1 second of inactivity
      this.typingTimer = setTimeout(() => {
        this.stopTyping();
      }, 1000);
    }
  }

  stopTyping() {
    if (this.isConnected && this.stompClient && this.chatId) {
      this.stompClient.publish({
        destination: `/app/chat/${this.chatId}/typing`,
        body: JSON.stringify({ username: 'User', typing: false })
      });
    }
  }

  toggleConnection() {
    if (this.isConnected) {
      this.disconnect();
    } else {
      this.connectWebSocket();
    }
  }

  disconnect() {
    if (this.stompClient) {
      this.stompClient.deactivate();
      this.stompClient = null;
    }
    this.isConnected = false;
    this.connectionStatus = 'Disconnected';
  }

  getSenderName(msg: any): string {
    if (msg.senderType === 'admin') {
      return 'Admin';
    } else if (msg.user && msg.user.name) {
      return msg.user.name;
    } else {
      return 'User';
    }
  }

  scrollToBottom() {
    setTimeout(() => {
      const chatContainer = document.querySelector('.chat-messages');
      if (chatContainer) {
        chatContainer.scrollTop = chatContainer.scrollHeight;
      }
    }, 100);
  }
}
