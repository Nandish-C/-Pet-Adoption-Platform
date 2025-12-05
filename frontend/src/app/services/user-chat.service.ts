import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, Subject, interval } from 'rxjs';
import { switchMap } from 'rxjs/operators';

export interface UserChat {
  id: string;
  startedAt: string;
  endedAt?: string;
  status: string;
  chatType: string;
  initiator: any;
  recipient: any;
  pet?: any;
  messages?: UserMessage[];
}

export interface UserMessage {
  id: string;
  content: string;
  timestamp: string;
  isRead: boolean;
  sender: any;
  userChat: UserChat;
}

export interface UserMessageDTO {
  id: string;
  content: string;
  senderId: string;
  senderName: string;
  chatId: string;
  timestamp: Date;
  read: boolean;
}

export interface UserChatNotification {
  type: string;
  message: string;
  chatId: string;
  timestamp: Date;
}

@Injectable({
  providedIn: 'root'
})
export class UserChatService {
  private apiUrl = 'http://localhost:8080/api/user-chats';
  private pollingInterval: any;
  private currentChatId: string | null = null;

  // Subjects for message updates
  private messageSubject = new Subject<UserMessage[]>();

  constructor(private http: HttpClient) { }

  private getHeaders(): HttpHeaders {
    const token = localStorage.getItem('token');
    return new HttpHeaders({
      'Content-Type': 'application/json',
      'Authorization': token ? `Bearer ${token}` : ''
    });
  }

  // Start a new direct chat between two users
  startDirectChat(initiatorId: string, recipientId: string): Observable<UserChat> {
    return this.http.post<UserChat>(`${this.apiUrl}/start`, {
      initiatorId,
      recipientId
    }, { headers: this.getHeaders() });
  }

  // Send a message in a chat (REST API fallback)
  sendMessageRest(chatId: string, senderId: string, content: string): Observable<UserMessage> {
    return this.http.post<UserMessage>(`${this.apiUrl}/${chatId}/messages`, {
      senderId,
      content
    }, { headers: this.getHeaders() });
  }

  // Get all messages for a chat
  getChatMessages(chatId: string): Observable<UserMessage[]> {
    return this.http.get<UserMessage[]>(`${this.apiUrl}/${chatId}/messages`, { headers: this.getHeaders() });
  }

  // Get all chats for a user
  getUserChats(userId: string): Observable<UserChat[]> {
    return this.http.get<UserChat[]>(`${this.apiUrl}/user/${userId}`, { headers: this.getHeaders() });
  }

  // Get active chats for a user
  getActiveUserChats(userId: string): Observable<UserChat[]> {
    return this.http.get<UserChat[]>(`${this.apiUrl}/user/${userId}/active`, { headers: this.getHeaders() });
  }

  // Mark messages as read
  markMessagesAsRead(chatId: string, userId: string): Observable<any> {
    return this.http.put(`${this.apiUrl}/${chatId}/read`, {
      userId
    }, { headers: this.getHeaders() });
  }

  // Get unread message count for a user
  getUnreadMessageCount(userId: string): Observable<{unreadCount: number}> {
    return this.http.get<{unreadCount: number}>(`${this.apiUrl}/user/${userId}/unread-count`, { headers: this.getHeaders() });
  }

  // End a chat
  endChat(chatId: string): Observable<UserChat> {
    return this.http.put<UserChat>(`${this.apiUrl}/${chatId}/end`, {}, { headers: this.getHeaders() });
  }

  // Get chat by ID
  getChatById(chatId: string): Observable<UserChat> {
    return this.http.get<UserChat>(`${this.apiUrl}/${chatId}`, { headers: this.getHeaders() });
  }

  // Get all users for chat selection
  getAllUsers(): Observable<any[]> {
    return this.http.get<any[]>('http://localhost:8080/api/auth/users-for-chat', { headers: this.getHeaders() });
  }

  // Polling methods for message updates

  // Start polling for new messages in a chat
  startPolling(chatId: string): void {
    this.stopPolling(); // Stop any existing polling
    this.currentChatId = chatId;

    // Poll every 2 seconds for new messages
    this.pollingInterval = interval(2000).pipe(
      switchMap(() => this.getChatMessages(chatId))
    ).subscribe({
      next: (messages) => {
        console.log(`Polling chat ${chatId}: received ${messages.length} messages`);
        this.messageSubject.next(messages);
      },
      error: (error) => {
        console.error('Error polling messages:', error);
      }
    });
  }

  // Stop polling
  stopPolling(): void {
    if (this.pollingInterval) {
      this.pollingInterval.unsubscribe();
      this.pollingInterval = null;
    }
    this.currentChatId = null;
  }

  // Get message updates observable
  getMessageUpdates(): Observable<UserMessage[]> {
    return this.messageSubject.asObservable();
  }
}