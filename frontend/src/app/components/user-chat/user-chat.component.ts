import { Component, OnInit, OnDestroy, ViewChild, ElementRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { UserChatService, UserChat, UserMessage, UserMessageDTO } from '../../services/user-chat.service';
import { AuthService } from '../../services/auth.service';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-user-chat',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './user-chat.component.html',
  styleUrls: ['./user-chat.component.css']
})
export class UserChatComponent implements OnInit, OnDestroy {
  currentUser: any;
  chats: UserChat[] = [];
  selectedChat: UserChat | null = null;
  messages: UserMessage[] = [];
  newMessage: string = '';
  users: any[] = []; // For user selection
  selectedUserId: string | null = null; // Changed to string to match user IDs
  showNewChat = false;

  @ViewChild('messagesContainer') private messagesContainer!: ElementRef;

  private subscriptions: Subscription[] = [];

  constructor(
    private userChatService: UserChatService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    // Get current user from AuthService
    this.currentUser = this.authService.getCurrentUser();

    // If currentUser is not available, try to get from localStorage
    if (!this.currentUser) {
      const userId = localStorage.getItem('userId');
      const userRole = localStorage.getItem('userRole');
      const username = localStorage.getItem('username');

      if (userId && userRole) {
        this.currentUser = {
          id: userId,
          role: userRole,
          name: username || 'User',
          email: userId // Use userId as email for admin users
        };
      }
    }

    // If still no current user, redirect to login
    if (!this.currentUser) {
      console.error('No current user found, redirecting to login');
      // You might want to redirect to login here
      return;
    }

    this.loadUserChats();
    this.loadUsers();
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(sub => sub.unsubscribe());
    this.userChatService.stopPolling();
  }

  loadUserChats(): void {
    if (this.currentUser?.id) {
      const sub = this.userChatService.getUserChats(this.currentUser.id)
        .subscribe({
          next: (chats) => {
            this.chats = chats;
          },
          error: (error) => {
            console.error('Error loading chats:', error);
          }
        });
      this.subscriptions.push(sub);
    }
  }

  loadUsers(): void {
    // Load all users from the API
    const sub = this.userChatService.getAllUsers().subscribe({
      next: (users) => {
        // Filter out the current user and only show users (not admins for admin users)
        this.users = users.filter(user => user.id !== this.currentUser?.id);
      },
      error: (error) => {
        console.error('Error loading users:', error);
        // Fallback to empty array if API fails
        this.users = [];
      }
    });
    this.subscriptions.push(sub);
  }

  startNewChat(): void {
    if (this.selectedUserId) {
      const sub = this.userChatService.startDirectChat(this.currentUser.id, this.selectedUserId)
        .subscribe({
          next: (chat) => {
            this.chats.unshift(chat);
            this.selectedChat = chat;
            this.selectedUserId = null;
            this.showNewChat = false; // Close the new chat form
            this.loadMessages();
            this.scrollToBottom(); // Scroll to bottom after loading messages
          },
          error: (error) => {
            console.error('Error starting chat:', error);
          }
        });
      this.subscriptions.push(sub);
    }
  }

  selectChat(chat: UserChat): void {
    // Stop any existing polling
    this.userChatService.stopPolling();

    this.selectedChat = chat;
    this.loadMessages();

    // Start polling for new messages in this chat
    if (this.selectedChat?.id) {
      this.userChatService.startPolling(this.selectedChat.id);

      // Subscribe to message updates
      const messageSub = this.userChatService.getMessageUpdates()
        .subscribe((messages: UserMessage[]) => {
          console.log('Received polled messages:', messages.length, 'Current messages:', this.messages.length);

          // Always update messages when polling returns data
          // Compare message IDs to detect changes
          const currentIds = this.messages.map(m => m.id).sort();
          const polledIds = messages.map(m => m.id).sort();
          const hasChanges = JSON.stringify(currentIds) !== JSON.stringify(polledIds);

          if (hasChanges || messages.length !== this.messages.length) {
            console.log('Updating messages from polling - new/changed messages detected');
            this.messages = [...messages]; // Create a new array reference
            this.scrollToBottom();
          }
        });
      this.subscriptions.push(messageSub);
    }
  }

  loadMessages(): void {
    if (this.selectedChat?.id) {
      const sub = this.userChatService.getChatMessages(this.selectedChat.id)
        .subscribe({
          next: (messages) => {
            this.messages = messages;
            this.scrollToBottom(); // Scroll to bottom after loading messages
          },
          error: (error) => {
            console.error('Error loading messages:', error);
          }
        });
      this.subscriptions.push(sub);
    }
  }

  sendMessage(): void {
    if (this.newMessage.trim() && this.selectedChat?.id && this.currentUser?.id) {
      // Send message via REST API
      const sub = this.userChatService.sendMessageRest(this.selectedChat.id, this.currentUser.id, this.newMessage.trim())
        .subscribe({
          next: (message) => {
            this.messages.push(message);
            this.newMessage = '';
            this.scrollToBottom(); // Scroll to bottom after sending message
          },
          error: (error) => {
            console.error('Error sending message:', error);
          }
        });
      this.subscriptions.push(sub);
    }
  }

  private scrollToBottom(): void {
    setTimeout(() => {
      if (this.messagesContainer) {
        this.messagesContainer.nativeElement.scrollTop = this.messagesContainer.nativeElement.scrollHeight;
      }
    }, 100);
  }

  getChatDisplayName(chat: UserChat): string {
    if (chat.initiator.id === this.currentUser?.id) {
      return chat.recipient.name;
    } else {
      return chat.initiator.name;
    }
  }

  getMessageTime(timestamp: string): string {
    return new Date(timestamp).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
  }

  isMessageFromCurrentUser(message: UserMessage): boolean {
    return message.sender.id === this.currentUser?.id;
  }
}