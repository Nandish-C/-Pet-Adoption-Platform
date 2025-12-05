import { Component, OnInit } from '@angular/core';
import { NgIf, NgFor, NgClass, TitleCasePipe, SlicePipe, DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { InquiryService } from '../../services/inquiry.service';
import { AuthService } from '../../services/auth.service';

interface Pet {
  id: string;
  name: string;
  species: string;
  breed: string;
  age: number;
  description: string;
  status: string;
  imageUrl?: string;
  shelterId?: string;
  size?: string;
  energyLevel?: string;
  goodWithKids?: boolean;
  goodWithPets?: boolean;
  price?: number;
  maxBudget?: number;
  adoptionFee?: number;
}

interface User {
  id: string;
  name: string;
  email: string;
  role: string;
}

interface Adoption {
  id: string;
  userId: string;
  petId: string;
  status: string;
  createdAt: string;
}

interface Chat {
  id: string;
  userId: string;
  petId: string;
  startedAt: string;
  endedAt?: string;
  status: string;
}

interface Donation {
  id: string;
  amount: number;
  userId?: string;
  shelterId?: string;
  transactionToken?: string;
  timestamp: string;
}

interface Inquiry {
  id: string;
  message: string;
  userId?: string;
  petId?: string;
  chatId?: string;
  timestamp: string;
  senderType?: string;
}

interface Shelter {
  id: string;
  name: string;
  location: string;
  phone?: string;
  email?: string;
}

interface Payment {
  id: string;
  userId: string;
  petId: string;
  adoptionId?: string;
  amount: number;
  status: string;
  stripePaymentIntentId?: string;
  createdAt: string;
}

interface AdoptedPet {
  id: string;
  petId: string;
  petName: string;
  userId: string;
  userName: string;
  adoptionDate: string;
}

@Component({
  selector: 'app-admin',
  standalone: true,
  imports: [NgIf, NgFor, NgClass, TitleCasePipe, SlicePipe, DatePipe, FormsModule],
  templateUrl: './admin.component.html',
  styleUrls: ['./admin.component.css']
})
export class AdminComponent implements OnInit {
  pets: Pet[] = [];
  users: User[] = [];
  adoptions: Adoption[] = [];
  adoptedPets: AdoptedPet[] = [];
  chats: Chat[] = [];
  donations: Donation[] = [];
  inquiries: Inquiry[] = [];
  shelters: Shelter[] = [];
  payments: Payment[] = [];

  activeSection = 'dashboard';
  showForm = false;
  editingItem: any = null;

  newItem: any = {};

  dashboardStats = {
    totalPets: 0,
    totalUsers: 0,
    totalAdoptions: 0,
    totalDonations: 0,
    availablePets: 0,
    adoptedPets: 0,
    totalPayments: 0,
    totalRevenue: 0,
    successfulPayments: 0,
    refundedPayments: 0,
    pendingPayments: 0
  };

  selectedChat: any = null;
  chatMessages: any[] = [];
  newMessage: string = '';

  selectedFile: File | null = null;
  imagePreview: string | null = null;

  // Matching section
  matchingStats = {
    totalMatchesGenerated: 0,
    successfulMatches: 0,
    pendingMatches: 0
  };
  selectedInquiry: any = null;
  selectedPetForMatch: any = null;

  // Pet filtering for admin
  petFilters = {
    species: '',
    breed: '',
    ageRange: { min: 0, max: 20 },
    size: '',
    energyLevel: '',
    goodWithKids: false,
    goodWithPets: false,
    budget: 0,
    maxBudget: 0,
    status: ''
  };
  filteredPets: Pet[] = [];

  constructor(private http: HttpClient, private inquiryService: InquiryService, private authService: AuthService, private router: Router) {}

  private handleAuthError(err: any, action: string) {
    console.error(`Error ${action}:`, err);
    if (err.status === 403) {
      alert('Access denied. Your session may have expired. Please log in again.');
      this.authService.logout();
      this.router.navigate(['/login']);
    } else {
      alert(`Failed to ${action}. Please try again.`);
    }
  }

  ngOnInit() {
    console.log('AdminComponent ngOnInit called');

    // Check if user is admin
    const currentUser = this.authService.getCurrentUser();
    const userRole = localStorage.getItem('userRole');

    console.log('AdminComponent - currentUser:', currentUser);
    console.log('AdminComponent - userRole:', userRole);

    if (!currentUser || currentUser.role !== 'ADMIN') {
      if (userRole !== 'ADMIN') {
        console.log('User is not admin, redirecting to login');
        this.router.navigate(['/login']);
        return;
      }
    }

    this.loadDashboardStats();
    this.loadMatchingStats();
  }

  loadDashboardStats() {
    this.loadPets();
    this.loadUsers();
    this.loadAdoptions();
    this.loadAdoptedPets();
    this.loadChats();
    this.loadDonations();
    this.loadInquiries();
    this.loadShelters();
    this.loadPayments();
  }

  loadAdoptions() {
    this.http.get<Adoption[]>('http://localhost:8080/api/admin/adoptions').subscribe({
      next: (adoptions) => {
        this.adoptions = adoptions;
        this.updateDashboardStats();
      },
      error: (err) => {
        console.error('Error loading adoptions:', err);
        alert('Failed to load adoptions. Please check your authentication and try again.');
      }
    });
  }

  loadAdoptedPets() {
    this.http.get<AdoptedPet[]>('http://localhost:8080/api/admin/adopted-pets').subscribe({
      next: (adoptedPets) => {
        this.adoptedPets = adoptedPets;
      },
      error: (err) => {
        console.error('Error loading adopted pets:', err);
        alert('Failed to load adopted pets. Please check your authentication and try again.');
      }
    });
  }

  loadChats() {
    this.http.get<Chat[]>('http://localhost:8080/api/admin/chats').subscribe({
      next: (chats) => this.chats = chats,
      error: (err) => {
        console.error('Error loading chats:', err);
        alert('Failed to load chats. Please check your authentication and try again.');
      }
    });
  }

  loadDonations() {
    this.http.get<Donation[]>('http://localhost:8080/api/admin/donations').subscribe({
      next: (donations) => {
        this.donations = donations;
        this.updateDashboardStats();
      },
      error: (err) => {
        console.error('Error loading donations:', err);
        alert('Failed to load donations. Please check your authentication and try again.');
      }
    });
  }

  loadInquiries() {
    this.http.get<any[]>('http://localhost:8080/api/admin/inquiries').subscribe({
      next: (inquiries) => this.inquiries = inquiries,
      error: (err) => {
        console.error('Error loading inquiries:', err);
        alert('Failed to load inquiries. Please check your authentication and try again.');
      }
    });
  }

  loadShelters() {
    this.http.get<Shelter[]>('http://localhost:8080/api/admin/shelters').subscribe({
      next: (shelters) => this.shelters = shelters,
      error: (err) => {
        console.error('Error loading shelters:', err);
        alert('Failed to load shelters. Please check your authentication and try again.');
      }
    });
  }

  loadPayments() {
    this.http.get<Payment[]>('http://localhost:8080/api/admin/payments').subscribe({
      next: (payments) => {
        this.payments = payments;
        this.updateDashboardStats();
      },
      error: (err) => {
        console.error('Error loading payments:', err);
        alert('Failed to load payments. Please check your authentication and try again.');
      }
    });
  }

  loadPets() {
    this.http.get<Pet[]>('http://localhost:8080/api/admin/pets').subscribe({
      next: (pets) => {
        this.pets = pets;
        this.applyPetFilters();
        this.updateDashboardStats();
      },
      error: (err: any) => {
        console.error('Error loading pets:', err);
        if (err.status === 403) {
          alert('Access denied. Your session may have expired. Please log in again.');
          this.authService.logout();
          this.router.navigate(['/login']);
        } else {
          alert('Failed to load pets. Please try again.');
        }
      }
    });
  }

  applyPetFilters() {
    this.filteredPets = this.pets.filter(pet => {
      // Status filter
      if (this.petFilters.status && pet.status !== this.petFilters.status) {
        return false;
      }

      // Species filter
      if (this.petFilters.species && !pet.species.toLowerCase().includes(this.petFilters.species.toLowerCase())) {
        return false;
      }

      // Breed filter
      if (this.petFilters.breed && (!pet.breed || !pet.breed.toLowerCase().includes(this.petFilters.breed.toLowerCase()))) {
        return false;
      }

      // Age range filter
      if (pet.age < this.petFilters.ageRange.min || pet.age > this.petFilters.ageRange.max) {
        return false;
      }

      // Size filter (if available)
      if (this.petFilters.size && pet.size !== this.petFilters.size) {
        return false;
      }

      // Energy level filter (if available)
      if (this.petFilters.energyLevel && pet.energyLevel !== this.petFilters.energyLevel) {
        return false;
      }

      // Good with kids filter (if available)
      if (this.petFilters.goodWithKids && pet.goodWithKids !== this.petFilters.goodWithKids) {
        return false;
      }

      // Good with pets filter (if available)
      if (this.petFilters.goodWithPets && pet.goodWithPets !== this.petFilters.goodWithPets) {
        return false;
      }

      // Budget filter (if available)
      if (this.petFilters.budget > 0 && pet.price && pet.price > this.petFilters.budget) {
        return false;
      }

      // Max Budget filter (if available)
      if (this.petFilters.maxBudget > 0 && pet.maxBudget && pet.maxBudget > this.petFilters.maxBudget) {
        return false;
      }

      return true;
    });
  }

  clearPetFilters() {
    this.petFilters = {
      species: '',
      breed: '',
      ageRange: { min: 0, max: 20 },
      size: '',
      energyLevel: '',
      goodWithKids: false,
      goodWithPets: false,
      budget: 0,
      maxBudget: 0,
      status: ''
    };
    this.applyPetFilters();
  }

  loadUsers() {
    this.http.get<User[]>('http://localhost:8080/api/admin/users').subscribe({
      next: (users) => {
        this.users = users;
        this.updateDashboardStats();
      },
      error: (err) => {
        console.error('Error loading users:', err);
        alert('Failed to load users. Please check your authentication and try again.');
      }
    });
  }

  updateDashboardStats() {
    this.dashboardStats = {
      totalPets: this.pets.length,
      totalUsers: this.users.length,
      totalAdoptions: this.adoptions.length,
      totalDonations: this.donations.length,
      availablePets: this.pets.filter(pet => pet.status === 'available').length,
      adoptedPets: this.pets.filter(pet => pet.status === 'adopted').length,
      totalPayments: this.payments.length,
      totalRevenue: this.payments.filter(p => p.status === 'succeeded').reduce((sum, p) => sum + p.amount, 0),
      successfulPayments: this.payments.filter(p => p.status === 'succeeded').length,
      refundedPayments: this.payments.filter(p => p.status === 'refunded').length,
      pendingPayments: this.payments.filter(p => p.status === 'pending').length
    };
  }

  setActiveSection(section: string) {
    this.activeSection = section;
    this.showForm = false;
    this.editingItem = null;
    this.newItem = {};
  }

  showAddForm(section: string) {
    this.activeSection = section;
    this.showForm = true;
    this.editingItem = null;
    this.initializeNewItem(section);
  }

  initializeNewItem(section: string) {
    switch (section) {
      case 'pets':
        this.newItem = {
          name: '',
          species: '',
          breed: '',
          age: 0,
          description: '',
          status: 'available',
          size: '',
          energyLevel: '',
          maxBudget: 0,
          adoptionFee: 0
        };
        this.selectedFile = null;
        this.imagePreview = null;
        break;
      case 'users':
        this.newItem = { name: '', email: '', password: '', role: 'USER' };
        break;
      case 'shelters':
        this.newItem = { name: '', location: '', phone: '', email: '' };
        break;
      case 'adoptions':
        this.newItem = { userId: '', petId: '', status: 'pending' };
        break;
      case 'donations':
        this.newItem = { amount: 0, userId: null, shelterId: null };
        break;
      case 'inquiries':
        this.newItem = { message: '', userId: null, petId: null, chatId: null };
        break;
      case 'payments':
        this.newItem = { userId: '', petId: '', adoptionId: '', amount: 0, status: 'pending' };
        break;
      default:
        this.newItem = {};
    }
  }

  saveItem(section: string) {
    if (this.editingItem) {
      this.updateItem(section);
    } else {
      this.addItem(section);
    }
  }

  addItem(section: string) {
    this.http.post(`http://localhost:8080/api/admin/${section}`, this.newItem).subscribe({
      next: (item: any) => {
        if (section === 'pets' && this.selectedFile) {
          this.uploadPetImage(item.id);
        } else {
          this.loadDataForSection(section);
          this.showForm = false;
          this.newItem = {};
          this.selectedFile = null;
          this.imagePreview = null;
        }
      },
      error: (err) => {
        console.error(`Error adding ${section}:`, err);
        alert(`Failed to add ${section.slice(0, -1)}. Please check your authentication and try again.`);
      }
    });
  }

  editItem(section: string, item: any) {
    this.activeSection = section;
    this.editingItem = { ...item };
    this.showForm = true;
  }

  updateItem(section: string) {
    if (this.editingItem) {
      this.http.put(`http://localhost:8080/api/admin/${section}/${this.editingItem.id}`, this.editingItem).subscribe({
        next: (updatedItem: any) => {
          if (section === 'pets' && this.selectedFile) {
            this.uploadPetImage(this.editingItem.id);
          } else {
            this.loadDataForSection(section);
            this.showForm = false;
            this.editingItem = null;
            this.selectedFile = null;
            this.imagePreview = null;
          }
        },
        error: (err) => {
          console.error(`Error updating ${section}:`, err);
          alert(`Failed to update ${section.slice(0, -1)}. Please check your authentication and try again.`);
        }
      });
    }
  }

  deleteItem(section: string, id: any) {
    if (confirm('Are you sure you want to delete this item?')) {
      this.http.delete(`http://localhost:8080/api/admin/${section}/${id}`).subscribe({
        next: () => this.loadDataForSection(section),
        error: (err) => {
          console.error(`Error deleting ${section}:`, err);
          alert(`Failed to delete ${section.slice(0, -1)}. Please check your authentication and try again.`);
        }
      });
    }
  }

  loadDataForSection(section: string) {
    switch (section) {
      case 'pets': this.loadPets(); break;
      case 'users': this.loadUsers(); break;
      case 'adoptions': this.loadAdoptions(); break;
      case 'chats': this.loadChats(); break;
      case 'donations': this.loadDonations(); break;
      case 'inquiries': this.loadInquiries(); break;
      case 'shelters': this.loadShelters(); break;
      case 'payments': this.loadPayments(); break;
    }
  }

  cancelForm() {
    this.showForm = false;
    this.editingItem = null;
    this.newItem = {};
    this.selectedFile = null;
    this.imagePreview = null;
  }

  onFileSelected(event: any) {
    const file = event.target.files[0];
    if (file) {
      // Check file type
      if (!file.type.startsWith('image/')) {
        alert('Please select a valid image file.');
        return;
      }
      // Check file size (e.g., 5MB)
      if (file.size > 5 * 1024 * 1024) {
        alert('File size must be less than 5MB.');
        return;
      }
      this.selectedFile = file;
      const reader = new FileReader();
      reader.onload = (e) => {
        this.imagePreview = e.target?.result as string;
      };
      reader.readAsDataURL(file);
    }
  }

  uploadPetImage(petId: string) {
    if (!this.selectedFile) {
      return;
    }

    const formData = new FormData();
    formData.append('image', this.selectedFile);

    this.http.post(`http://localhost:8080/api/pets/${petId}/upload-image`, formData).subscribe({
      next: (response: any) => {
        console.log('Image uploaded successfully:', response);
        this.loadPets(); // Refresh pets list to show new image
        this.selectedFile = null;
        this.imagePreview = null;
        this.showForm = false;
        if (this.editingItem) {
          this.editingItem = null;
        }
        this.newItem = {};
      },
      error: (err) => {
        console.error('Error uploading image:', err);
        alert('Failed to upload image. Please try again.');
      }
    });
  }

  getCurrentDataArray() {
    switch (this.activeSection) {
      case 'pets': return this.pets;
      case 'users': return this.users;
      case 'adoptions': return this.adoptions;
      case 'chats': return this.chats;
      case 'donations': return this.donations;
      case 'inquiries': return this.inquiries;
      case 'shelters': return this.shelters;
      case 'payments': return this.payments;
      default: return [];
    }
  }

  replyToChat(chat: Chat) {
    this.openChat(chat);
  }

  openChat(chat: Chat) {
    this.selectedChat = chat;
    this.loadChatMessages(chat.id);
  }

  loadChatMessages(chatId: string) {
    this.http.get<any[]>(`http://localhost:8080/api/admin/inquiries?chatId=${chatId}`).subscribe({
      next: (messages) => {
        this.chatMessages = messages.sort((a, b) => new Date(a.timestamp).getTime() - new Date(b.timestamp).getTime());
      },
      error: (err) => {
        console.error('Error loading chat messages:', err);
        this.chatMessages = [];
      }
    });
  }

  sendReply() {
    if (!this.newMessage.trim() || !this.selectedChat) {
      return;
    }
    const replyData = {
      chatId: this.selectedChat.id,
      message: this.newMessage.trim()
    };
    this.http.post('http://localhost:8080/api/admin/chats/reply', replyData).subscribe({
      next: (response) => {
        this.newMessage = '';
        this.loadChatMessages(this.selectedChat.id);
      },
      error: (err) => {
        console.error('Error sending reply:', err);
        alert('Failed to send reply. Please try again.');
      }
    });
  }

  closeChatModal() {
    this.selectedChat = null;
    this.chatMessages = [];
    this.newMessage = '';
  }

  navigateToUserChat() {
    this.router.navigate(['/user-chat']);
  }

  generateReport() {
    const report = {
      generatedAt: new Date().toISOString(),
      stats: this.dashboardStats,
      data: {
        pets: this.pets,
        users: this.users,
        adoptions: this.adoptions,
        donations: this.donations,
        shelters: this.shelters,
        payments: this.payments
      }
    };

    const blob = new Blob([JSON.stringify(report, null, 2)], { type: 'application/json' });
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `admin-report-${new Date().toISOString().split('T')[0]}.json`;
    a.click();
    window.URL.revokeObjectURL(url);
  }

  // Manual matching methods
  selectInquiryForMatch(inquiry: any) {
    this.selectedInquiry = inquiry;
    this.selectedPetForMatch = null;
  }

  selectPetForMatch(pet: any) {
    this.selectedPetForMatch = pet;
  }

  assignPetToInquiry() {
    if (!this.selectedInquiry || !this.selectedPetForMatch) {
      alert('Please select both an inquiry and a pet.');
      return;
    }

    const matchData = {
      inquiryId: this.selectedInquiry.id,
      petId: this.selectedPetForMatch.id,
      userId: this.selectedInquiry.userId
    };

    this.http.post('http://localhost:8080/api/admin/manual-match', matchData).subscribe({
      next: (response: any) => {
        alert('Pet assigned successfully!');
        this.selectedInquiry = null;
        this.selectedPetForMatch = null;
        this.loadInquiries();
        this.loadPets();
        this.loadMatchingStats();
      },
      error: (err) => {
        console.error('Error assigning pet:', err);
        alert('Failed to assign pet. Please try again.');
      }
    });
  }

  loadMatchingStats() {
    this.http.get<any>('http://localhost:8080/api/admin/matching-stats').subscribe({
      next: (stats) => {
        this.matchingStats = stats;
      },
      error: (err) => {
        console.error('Error loading matching stats:', err);
        // Set default stats if endpoint doesn't exist
        this.matchingStats = {
          totalMatchesGenerated: this.adoptions.length,
          successfulMatches: this.adoptions.filter(a => a.status === 'completed').length,
          pendingMatches: this.adoptions.filter(a => a.status === 'pending').length
        };
      }
    });
  }

  cancelMatch() {
    this.selectedInquiry = null;
    this.selectedPetForMatch = null;
  }

  approveAdoption(adoption: Adoption) {
    if (confirm('Are you sure you want to approve this adoption?')) {
      this.http.put(`http://localhost:8080/api/admin/adoptions/${adoption.id}/approve`, {}).subscribe({
        next: () => {
          this.loadAdoptions();
          this.loadAdoptedPets();
          alert('Adoption approved successfully!');
        },
        error: (err) => {
          console.error('Error approving adoption:', err);
          alert('Failed to approve adoption. Please try again.');
        }
      });
    }
  }

  rejectAdoption(adoption: Adoption) {
    if (confirm('Are you sure you want to reject this adoption?')) {
      this.http.put(`http://localhost:8080/api/admin/adoptions/${adoption.id}/reject`, {}).subscribe({
        next: () => {
          this.loadAdoptions();
          alert('Adoption rejected successfully!');
        },
        error: (err) => {
          console.error('Error rejecting adoption:', err);
          alert('Failed to reject adoption. Please try again.');
        }
      });
    }
  }
}
