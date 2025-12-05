import { Routes } from '@angular/router';
import { ChatComponent } from './components/chat/chat.component';
import { GeneralChatComponent } from './components/general-chat/general-chat.component';
import { AdminComponent } from './components/admin/admin.component';
import { PaymentComponent } from './components/payment/payment.component';
import { LoginComponent } from './components/auth/login.component';
import { RegisterComponent } from './components/auth/register.component';
import { HomeComponent } from './components/home/home.component';
import { PaymentHistoryComponent } from './components/payment-history/payment-history.component';
import { AdoptionsComponent } from './components/adoptions/adoptions.component';
import { InquiriesComponent } from './components/inquiries/inquiries.component';
import { MatchComponent } from './components/match/match.component';
import { DonateComponent } from './components/donate/donate.component';
import { SheltersComponent } from './components/shelters/shelters.component';
import { UserChatComponent } from './components/user-chat/user-chat.component';
import { ShelterDetailComponent } from './components/shelter-detail/shelter-detail.component';
import { AuthGuard } from './guards/auth.guard';
import { AdminGuard } from './guards/admin.guard';

export const routes: Routes = [
	{ path: 'chat/:id', component: ChatComponent },
	{ path: 'chat', redirectTo: '/chat/1', pathMatch: 'full' },
	{ path: 'general-chat', component: GeneralChatComponent, canActivate: [AuthGuard] },
	{ path: 'admin', component: AdminComponent, canActivate: [AdminGuard] },
	{ path: 'payment/:petId', component: PaymentComponent, canActivate: [AuthGuard] },
	{ path: 'payment', redirectTo: '/payment-history', pathMatch: 'full' },
	{ path: 'login', component: LoginComponent },
	{ path: 'register', component: RegisterComponent },
	{ path: 'home', component: HomeComponent },
	{ path: 'payment-history', component: PaymentHistoryComponent },
	{ path: 'adoptions', component: AdoptionsComponent },
	{ path: 'inquiries', component: InquiriesComponent },
	{ path: 'match-pets', component: MatchComponent },
	{ path: 'donate', component: DonateComponent, canActivate: [AuthGuard] },
	{ path: 'shelters', component: SheltersComponent },
	{ path: 'shelter/:id', component: ShelterDetailComponent },
	{ path: 'user-chat', component: UserChatComponent },
	{ path: '', redirectTo: '/home', pathMatch: 'full' },
	// Add more routes here as needed
];
