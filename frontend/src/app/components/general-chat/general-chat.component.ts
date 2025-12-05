import { Component, OnInit, OnDestroy } from '@angular/core';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import { InquiryService } from '../../services/inquiry.service';
import { HttpClient } from '@angular/common/http';

import { CommonModule, DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-general-chat',
  standalone: true,
  imports: [CommonModule, FormsModule, DatePipe],
  template: `
    <div class="card mt-4">
      <div class="card-body">
        <h3>General Support Chat</h3>
        <div class="mb-3">
          <small class="text-muted">Connection: {{ connectionStatus }}</small>
          <span *ngIf="isTyping" class="ms-2 text-info">Someone is typing...</span>
        </div>

        <div class="chat-messages" style="height: 300px; overflow-y: auto; border: 1px solid #ddd; padding: 10px; margin-bottom: 10px;">
          <div *ngFor="let msg of messages" class="mb-2">
            <strong>{{ getSenderName(msg) }}:</strong> {{ msg.message }}
            <small class="text-muted">({{ msg.timestamp | date:'short' }})</small>
          </div>
          <div *ngIf="messages.length === 0" class="text-center text-muted">
            No messages yet. Start a conversation with our support team!
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
export class GeneralChatComponent implements OnInit, OnDestroy {
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
    private inquiryService: InquiryService,
    private http: HttpClient
  ) {}

  ngOnInit() {
    this.initializeChat();
  }

  ngOnDestroy() {
    this.disconnect();
    if (this.pollingInterval) {
      clearInterval(this.pollingInterval);
    }
  }

  initializeChat() {
    // Get user ID from localStorage
    const userId = localStorage.getItem('userId') || 'guest';

    console.log('General Chat initialization debug:');
    console.log('userId from localStorage:', userId);

    // Try to start or find existing general chat
    this.http.post<any>(`http://localhost:8080/api/chat/start-general?userId=${userId}`, {})
      .subscribe({
        next: (chat) => {
          this.chatId = chat.id;
          this.loadChatHistory();
          this.connectWebSocket();
          this.startPollingFallback();
        },
        error: (err) => {
          console.error('Failed to start general chat:', err);
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

        // Subscribe to general chat messages
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
    const userId = localStorage.getItem('userId') || 'guest';

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
      return 'Support Agent';
    } else if (msg.user && msg.user.name) {
      return msg.user.name;
    } else {
      return 'You';
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