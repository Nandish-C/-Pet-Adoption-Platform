export interface Payment {
  id: string;
  userId: string;
  petId: string;
  amount: number;
  currency: string;
  displayAmount: number;
  status: string;
  stripePaymentIntentId: string;
  createdAt: string;
}
