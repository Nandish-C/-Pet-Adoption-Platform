-- MongoDB Sample Data Insertion Script
-- Run these commands in MongoDB shell (mongosh) connected to the pet_adoption database
-- Note: This uses simple string IDs for consistency; in production, use ObjectId()

use pet_adoption;

-- Clear existing data (optional, for clean start)
db.users.drop();
db.pets.drop();
db.shelters.drop();
db.adoptions.drop();
db.chats.drop();
db.inquiries.drop();
db.donations.drop();
db.payments.drop();
db.adopted_pets.drop();

// Insert Users (with ADMIN role for testing)
db.users.insertMany([
  {
    _id: "user1",
    name: "John Doe",
    email: "john@example.com",
    password: "$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi", // password: "password"
    role: "USER"
  },
  {
    _id: "admin1",
    name: "Admin User",
    email: "admin@example.com",
    password: "$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi", // password: "password"
    role: "ADMIN"
  }
]);

// Insert Shelters
db.shelters.insertMany([
  {
    _id: "shelter1",
    name: "City Animal Shelter",
    location: "New York, NY"
  }
]);

// Insert Pets (one available, one adopted)
db.pets.insertMany([
  {
    _id: "pet1",
    name: "Buddy",
    species: "Dog",
    breed: "Golden Retriever",
    age: 2,
    description: "Friendly dog looking for a home",
    status: "available",
    imageUrl: "https://example.com/buddy.jpg",
    shelterId: "shelter1",
    price: 15000.0,
    adoptionFee: 2000.0,
    size: "large",
    energyLevel: "high",
    goodWithKids: true,
    goodWithPets: true
  },
  {
    _id: "pet2",
    name: "Whiskers",
    species: "Cat",
    breed: "Siamese",
    age: 1,
    description: "Playful kitten",
    status: "adopted",
    imageUrl: "https://example.com/whiskers.jpg",
    shelterId: "shelter1",
    price: 8000.0,
    adoptionFee: 1500.0,
    size: "small",
    energyLevel: "medium",
    goodWithKids: true,
    goodWithPets: false
  }
]);

// Insert Chats and Inquiries (referencing pet1 with string ID)
db.chats.insertOne({
  _id: "chat1",
  userId: "user1",
  petId: "pet1",
  startedAt: ISODate("2024-01-01T10:00:00Z"),
  endedAt: null,
  status: "active"
});

db.inquiries.insertMany([
  {
    _id: "inquiry1",
    message: "Interested in adopting Buddy",
    userId: "user1",
    petId: "pet1",
    chatId: "chat1",
    timestamp: ISODate("2024-01-01T10:05:00Z"),
    senderType: "user"
  }
]);

// Insert a Pending Adoption (for testing approval flow)
db.adoptions.insertOne({
  _id: "adoption1",
  userId: "user1",
  petId: "pet1",
  status: "pending",
  createdAt: ISODate("2024-01-02T12:00:00Z")
});

// Insert Donations and Payments (sample)
db.donations.insertOne({
  _id: "donation1",
  amount: 50.0,
  userId: "user1",
  shelterId: "shelter1",
  transactionToken: "txn_123",
  timestamp: ISODate("2024-01-03T14:00:00Z")
});

db.payments.insertOne({
  _id: "payment1",
  userId: "user1",
  petId: null,
  adoptionId: null,
  amount: 50.0,
  status: "succeeded",
  stripePaymentIntentId: "pi_123",
  createdAt: ISODate("2024-01-03T14:01:00Z")
});

// No initial adopted_pets - will be created on approval

print("Sample data inserted successfully!");
