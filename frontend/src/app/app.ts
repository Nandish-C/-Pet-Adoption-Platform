import { Component, signal, OnInit } from '@angular/core';
import { PetService } from './services/pet.service';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import { ActivatedRoute, RouterOutlet, RouterModule, Router } from '@angular/router';
import { DonationService } from './services/donation.service';
import { AuthService } from './services/auth.service';
import { ShelterService } from './services/shelter.service';
import { AdoptionService } from './services/adoption.service';
import { InquiryService } from './services/inquiry.service';
import { CommonModule } from '@angular/common'; // For *ngFor
import { FormsModule } from '@angular/forms'; // For potential forms


// Define Pet interface
interface Pet {
  id: string;
  name: string;
  species: string;
  breed: string;
  age: number;
  description: string;
  status: string;
  shelterId?: string;
  videoUrl?: string;
  imageUrl?: string;
  size?: string;
  energyLevel?: string;
  goodWithKids?: boolean;
  goodWithPets?: boolean;
  specialNeeds?: boolean;
  adoptionFee?: number;
}

// Define User interface
interface User {
  name?: string;
  email: string;
  password: string;
  role?: string;
}

// Define Matching Criteria interface
interface MatchingCriteria {
  species?: string;
  breed?: string;
  ageRange: { min: number; max: number };
  size?: string;
  energyLevel?: string;
  goodWithKids?: boolean;
  goodWithPets?: boolean;
  location?: string;
  budget?: number;
}

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterOutlet, RouterModule],
  templateUrl: './app.html',
  styleUrls: ['./app.css']
})
export class App implements OnInit {
  protected readonly title = signal('Pet Adoption Platform');
  pets: Pet[] = [];
  stompClient: Client | null = null;
  donationAmount: number = 0;
  isMatch: boolean = false;
  isDonate: boolean = false;
  isLogin: boolean = false;
  isRegister: boolean = false;
  isChat: boolean = false;
  isShelter: boolean = false;
  isInquiry: boolean = false;
  isAdoptions: boolean = false;
  isAdmin: boolean = false;
  loggedIn: boolean = false;
  username: string = '';
  userRole: string = '';
  user: User = { email: '', password: '' };
  chatMessages: { sender: string, text: string }[] = [];
  newMessage: string = '';
  shelters: any[] = [];
  adoptions: any[] = [];
  inquiries: any[] = [];
  currentTheme: string = 'light';

  // Advanced matching properties
  matchingCriteria: MatchingCriteria = {
    ageRange: { min: 0, max: 20 }
  };
  matchedPets: Pet[] = [];
  isLoading: boolean = false;



  adoptPet(id: string) {
    if (!this.loggedIn) {
      alert('Please login to adopt a pet.');
      this.showLogin();
      return;
    }
    // Redirect to payment page instead of direct adoption
    this.router.navigate(['/payment', id]);
  }

  makeDonation() {
    if (this.donationAmount > 0) {
      const paymentRequest = {
        amount: this.donationAmount * 100,
        token: 'tok_visa',
        userId: 1,
        shelterId: 1
      };
      this.donationService.processDonation(paymentRequest).subscribe(response => {
        console.log('Donation processed:', response);
      });
    }
  }

  showShelters() {
    this.isShelter = true;
    this.isLogin = false;
    this.isRegister = false;
    this.isDonate = false;
    this.isMatch = false;
    this.isChat = false;
    this.isInquiry = false;
    this.isAdoptions = false;
    this.shelterService.getAllShelters().subscribe(shelters => this.shelters = shelters);
  }

  // Add forgot password navigation
  showForgotPassword() {
    const email = prompt('Please enter your email address:');
    if (email && email.trim()) {
      this.authService.forgotPassword(email.trim()).subscribe({
        next: (response) => {
          alert('Password reset instructions have been sent to your email.');
          this.showLogin();
        },
        error: (err) => {
          console.error('Forgot password error:', err);
          let errorMessage = 'Failed to send password reset email';
          if (err.error && typeof err.error === 'string') {
            errorMessage = err.error;
          } else if (err.error && err.error.message) {
            errorMessage = err.error.message;
          } else if (err.status === 404) {
            errorMessage = 'Email address not found';
          } else if (err.status === 0) {
            errorMessage = 'Cannot connect to server. Please check if the backend is running.';
          }
          alert(errorMessage);
        }
      });
    }
  }

  showInquiries() {
    this.isInquiry = true;
    this.isShelter = false;
    this.isLogin = false;
    this.isRegister = false;
    this.isDonate = false;
    this.isMatch = false;
    this.isChat = false;
    this.isAdoptions = false;
    this.inquiryService.getAllInquiries().subscribe(inquiries => this.inquiries = inquiries);
  }

  showAdoptions() {
    this.isAdoptions = true;
    this.isInquiry = false;
    this.isShelter = false;
    this.isLogin = false;
    this.isRegister = false;
    this.isDonate = false;
    this.isMatch = false;
    this.isChat = false;
    this.adoptionService.getAllAdoptions().subscribe(adoptions => this.adoptions = adoptions);
  }

  showHome() {
    this.isAdoptions = false;
    this.isInquiry = false;
    this.isShelter = false;
    this.isLogin = false;
    this.isRegister = false;
    this.isDonate = false;
    this.isMatch = false;
    this.isChat = false;
    this.isAdmin = false;
  }

  showMatch() {
    this.isMatch = true;
    this.isAdoptions = false;
    this.isInquiry = false;
    this.isShelter = false;
    this.isLogin = false;
    this.isRegister = false;
    this.isDonate = false;
    this.isChat = false;
  }

  showDonate() {
    this.isDonate = true;
    this.isMatch = false;
    this.isAdoptions = false;
    this.isInquiry = false;
    this.isShelter = false;
    this.isLogin = false;
    this.isRegister = false;
    this.isChat = false;
  }

  showLogin() {
    this.isLogin = true;
    this.isDonate = false;
    this.isMatch = false;
    this.isAdoptions = false;
    this.isInquiry = false;
    this.isShelter = false;
    this.isRegister = false;
    this.isChat = false;
  }

  showRegister() {
    this.isRegister = true;
    this.isLogin = false;
    this.isDonate = false;
    this.isMatch = false;
    this.isAdoptions = false;
    this.isInquiry = false;
    this.isShelter = false;
    this.isChat = false;
  }

  // Add navigation from login to register and vice versa
  goToRegister() {
    this.isLogin = false;
    this.isRegister = true;
  }

  goToLogin() {
    this.isRegister = false;
    this.isLogin = true;
  }

  constructor(
    private petService: PetService,
    private route: ActivatedRoute,
    private donationService: DonationService,
    private authService: AuthService,
    private shelterService: ShelterService,
    private adoptionService: AdoptionService,
    private inquiryService: InquiryService,
    private router: Router
  ) {}

  ngOnInit() {
    // Check for existing authentication token
    const token = localStorage.getItem('authToken');
    if (token) {
      this.loggedIn = true;
      this.username = localStorage.getItem('username') || 'User';
      this.userRole = localStorage.getItem('userRole') || 'USER';
      // If admin user, show admin dashboard by default
      if (this.userRole === 'ADMIN') {
        this.showAdminDashboard();
      }
    }

    // Subscribe to auth changes
    this.authService.currentUser$.subscribe(user => {
      if (user) {
        this.loggedIn = true;
        this.username = localStorage.getItem('username') || user.name || user.email || 'User';
        this.userRole = localStorage.getItem('userRole') || user.role || 'USER';
        if (this.userRole === 'ADMIN') {
          this.showAdminDashboard();
        }
      } else {
        this.loggedIn = false;
        this.username = '';
        this.userRole = '';
      }
    });

    // Load theme preference
    const savedTheme = localStorage.getItem('theme');
    if (savedTheme) {
      this.currentTheme = savedTheme;
      this.applyTheme(savedTheme);
    }

    this.route.data.subscribe(data => {
      this.isMatch = !!data['match'];
      this.isDonate = !!data['donate'];
      this.isLogin = !!data['login'];
      this.isRegister = !!data['register'];
      if (this.isMatch) {
        const species = 'Dog';
        const age = 2;
        const location = 'Any';
        this.petService.matchPets(species, age, location).subscribe((pets: Pet[]) => this.pets = pets);
      } else {
        this.petService.getPets().subscribe((pets: Pet[]) => this.pets = pets);
      }
    });

    // STOMP over SockJS WebSocket connection
    this.stompClient = new Client({
      webSocketFactory: () => new SockJS('http://localhost:8080/ws'),
      reconnectDelay: 5000,
    });
    this.stompClient.onConnect = () => {
      this.stompClient?.subscribe('/topic/pet-status', (message) => {
        const msg = JSON.parse(message.body);
        const pet = this.pets.find(p => p.id === msg.id);
        if (pet) pet.status = msg.status;
        else this.pets.push(msg as Pet);
      });
    };
    this.stompClient.activate();
  }

  showChat() {
    this.isChat = true;
    this.isRegister = false;
    this.isLogin = false;
    this.isDonate = false;
    this.isMatch = false;
    this.isAdoptions = false;
    this.isInquiry = false;
    this.isShelter = false;
    // Navigate to general support chat
    this.router.navigate(['/general-chat']);
  }

  showAdminDashboard() {
    if (this.userRole !== 'ADMIN') {
      alert('Access denied. Admin privileges required.');
      return;
    }
    this.isAdmin = true;
    this.isAdoptions = false;
    this.isInquiry = false;
    this.isShelter = false;
    this.isLogin = false;
    this.isRegister = false;
    this.isDonate = false;
    this.isMatch = false;
    this.isChat = false;
  }

  showUserManagement() {
    if (this.userRole !== 'ADMIN') {
      alert('Access denied. Admin privileges required.');
      return;
    }
    this.isAdmin = true;
    this.isAdoptions = false;
    this.isInquiry = false;
    this.isShelter = false;
    this.isLogin = false;
    this.isRegister = false;
    this.isDonate = false;
    this.isMatch = false;
    this.isChat = false;
  }

  showPetManagement() {
    if (this.userRole !== 'ADMIN') {
      alert('Access denied. Admin privileges required.');
      return;
    }
    this.isAdmin = true;
    this.isAdoptions = false;
    this.isInquiry = false;
    this.isShelter = false;
    this.isLogin = false;
    this.isRegister = false;
    this.isDonate = false;
    this.isMatch = false;
    this.isChat = false;
  }

  showReports() {
    if (this.userRole !== 'ADMIN') {
      alert('Access denied. Admin privileges required.');
      return;
    }
    this.isAdmin = true;
    this.isAdoptions = false;
    this.isInquiry = false;
    this.isShelter = false;
    this.isLogin = false;
    this.isRegister = false;
    this.isDonate = false;
    this.isMatch = false;
    this.isChat = false;
  }

  login() {
    this.authService.login(this.user).subscribe({
      next: (response: any) => {
        console.log('Logged in:', response);
        // Handle JWT token and user data
        if (response.token && response.user) {
          localStorage.setItem('authToken', response.token);
          localStorage.setItem('username', response.user.name || 'User');
          localStorage.setItem('userRole', response.user.role || 'USER');
          localStorage.setItem('userId', response.user.id?.toString() || '1');
          this.loggedIn = true;
          this.username = response.user.name || 'User';
          this.userRole = response.user.role || 'USER';
          this.isLogin = false;
          // If admin user, show admin dashboard by default
          if (this.userRole === 'ADMIN') {
            this.showAdminDashboard();
          }
          // Clear the user object to reset the form
          this.user = { email: '', password: '', role: '' };
        } else {
          // Fallback for old response format
          localStorage.setItem('username', response.name || 'User');
          localStorage.setItem('userId', '1'); // Fallback user ID
          this.loggedIn = true;
          this.username = response.name || 'User';
          this.isLogin = false;
          // Clear the user object to reset the form
          this.user = { email: '', password: '', role: '' };
        }
      },
      error: (err) => {
        console.error('Login error:', err);
        let errorMessage = 'Login failed';
        if (err.error && typeof err.error === 'string') {
          errorMessage = err.error;
        } else if (err.error && err.error.message) {
          errorMessage = err.error.message;
        } else if (err.status === 401) {
          errorMessage = 'Invalid email or password';
        } else if (err.status === 0) {
          errorMessage = 'Cannot connect to server. Please check if the backend is running.';
        }
        alert(errorMessage);
      }
    });
  }

  register() {
    this.authService.register(this.user).subscribe({
      next: (user) => {
        console.log('Registered:', user);
        alert('Registration successful! Please login with your credentials.');
        this.isRegister = false;
        this.isLogin = true;
        // Clear the user object to reset the form
        this.user = { email: '', password: '', role: '' };
      },
      error: (err) => {
        console.error('Registration error:', err);
        let errorMessage = 'Registration failed';
        if (err.error && typeof err.error === 'string') {
          errorMessage = err.error;
        } else if (err.error && err.error.message) {
          errorMessage = err.error.message;
        } else if (err.status === 400) {
          errorMessage = 'Email already exists or invalid data provided';
        } else if (err.status === 409) {
          errorMessage = 'Email already exists';
        } else if (err.status === 0) {
          errorMessage = 'Cannot connect to server. Please check if the backend is running.';
        }
        alert(errorMessage);
      }
    });
  }

  logout() {
    this.authService.logout();
    this.loggedIn = false;
    this.username = '';
    this.user = { email: '', password: '', role: '' };
    // Reset to home page
    this.showHome();
  }

  toggleTheme() {
    this.currentTheme = this.currentTheme === 'light' ? 'dark' : 'light';
    this.applyTheme(this.currentTheme);
    localStorage.setItem('theme', this.currentTheme);
  }

  private applyTheme(theme: string) {
    const body = document.body;
    if (theme === 'dark') {
      body.classList.add('dark-theme');
      body.classList.remove('light-theme');
    } else {
      body.classList.add('light-theme');
      body.classList.remove('dark-theme');
    }
  }

  sendMessage() {
    if (this.newMessage.trim()) {
      // Add user message to chat
      this.chatMessages.push({ sender: 'user', text: this.newMessage });

      // Send message via WebSocket if connected
      if (this.stompClient && this.stompClient.connected) {
        this.stompClient.publish({
          destination: '/app/chat',
          body: JSON.stringify({ message: this.newMessage, sender: this.username || 'User' })
        });
      }

      // Clear input
      this.newMessage = '';

      // Auto-scroll to bottom
      setTimeout(() => {
        const chatContainer = document.getElementById('chatMessages');
        if (chatContainer) {
          chatContainer.scrollTop = chatContainer.scrollHeight;
        }
      }, 100);
    }
  }

  findMatches() {
    this.isLoading = true;
    // Filter pets based on matching criteria
    this.matchedPets = this.pets.filter(pet => {
      let matches = true;

      if (this.matchingCriteria.species && pet.species !== this.matchingCriteria.species) {
        matches = false;
      }

      if (this.matchingCriteria.size && pet.size !== this.matchingCriteria.size) {
        matches = false;
      }

      if (this.matchingCriteria.energyLevel && pet.energyLevel !== this.matchingCriteria.energyLevel) {
        matches = false;
      }

      if (this.matchingCriteria.ageRange) {
        if (pet.age < this.matchingCriteria.ageRange.min || pet.age > this.matchingCriteria.ageRange.max) {
          matches = false;
        }
      }

      if (this.matchingCriteria.goodWithKids !== undefined && pet.goodWithKids !== this.matchingCriteria.goodWithKids) {
        matches = false;
      }

      if (this.matchingCriteria.goodWithPets !== undefined && pet.goodWithPets !== this.matchingCriteria.goodWithPets) {
        matches = false;
      }

      if (this.matchingCriteria.budget && pet.adoptionFee && pet.adoptionFee > this.matchingCriteria.budget) {
        matches = false;
      }

      return matches;
    });

    // Simulate loading delay
    setTimeout(() => {
      this.isLoading = false;
    }, 1000);
  }

  viewPetDetails(petId: string) {
    // Navigate to pet details or show modal
    console.log('Viewing details for pet:', petId);
    // For now, just show an alert
    const pet = this.pets.find(p => p.id === petId);
    if (pet) {
      alert(`Pet Details:\nName: ${pet.name}\nSpecies: ${pet.species}\nBreed: ${pet.breed}\nAge: ${pet.age}\nDescription: ${pet.description}`);
    }
  }
}
