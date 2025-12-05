const { MongoClient } = require('mongodb');

// Connection URL
const url = 'mongodb://localhost:27017';
const client = new MongoClient(url);

// Database Name
const dbName = 'pet_adoption';

async function main() {
  // Use connect method to connect to the server
  await client.connect();
  console.log('Connected successfully to server');

  const db = client.db(dbName);

  // Drop existing collections
  await db.collection('users').drop().catch(() => {});
  await db.collection('shelters').drop().catch(() => {});
  await db.collection('pets').drop().catch(() => {});
  await db.collection('adoptions').drop().catch(() => {});
  await db.collection('donations').drop().catch(() => {});
  await db.collection('inquiries').drop().catch(() => {});
  await db.collection('chats').drop().catch(() => {});
  await db.collection('payments').drop().catch(() => {});

  // Insert sample data
  await db.collection('users').insertMany([
    { _id: 1, name: "John Doe", email: "john@example.com", password: "$2a$10$examplehashedpassword", role: "USER", createdAt: new Date() },
    { _id: 2, name: "Jane Smith", email: "jane@example.com", password: "$2a$10$examplehashedpassword", role: "USER", createdAt: new Date() },
    { _id: 3, name: "Admin User", email: "admin@example.com", password: "$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi", role: "ADMIN", createdAt: new Date() },
    { _id: 4, name: "Bob Johnson", email: "bob@example.com", password: "$2a$10$examplehashedpassword", role: "USER", createdAt: new Date() },
    { _id: 5, name: "Alice Brown", email: "alice@example.com", password: "$2a$10$examplehashedpassword", role: "USER", createdAt: new Date() },
    { _id: 6, name: "Charlie Wilson", email: "charlie@example.com", password: "$2a$10$examplehashedpassword", role: "USER", createdAt: new Date() },
    { _id: 7, name: "Diana Davis", email: "diana@example.com", password: "$2a$10$examplehashedpassword", role: "USER", createdAt: new Date() },
    { _id: 8, name: "Eve Miller", email: "eve@example.com", password: "$2a$10$examplehashedpassword", role: "USER", createdAt: new Date() },
    { _id: 9, name: "Frank Garcia", email: "frank@example.com", password: "$2a$10$examplehashedpassword", role: "USER", createdAt: new Date() },
    { _id: 10, name: "Grace Lee", email: "grace@example.com", password: "$2a$10$examplehashedpassword", role: "USER", createdAt: new Date() }
  ]);

  await db.collection('shelters').insertMany([
    { _id: 1, name: "City Animal Shelter", location: "New York, NY" },
    { _id: 2, name: "Happy Paws Rescue", location: "Los Angeles, CA" },
    { _id: 3, name: "Furry Friends Adoption Center", location: "Chicago, IL" },
    { _id: 4, name: "Paw Haven", location: "Houston, TX" },
    { _id: 5, name: "Animal Care Society", location: "Miami, FL" },
    { _id: 6, name: "Urban Pet Rescue", location: "Seattle, WA" },
    { _id: 7, name: "Coastal Animal Haven", location: "San Diego, CA" },
    { _id: 8, name: "Mountain View Shelter", location: "Denver, CO" },
    { _id: 9, name: "River City Pets", location: "Portland, OR" },
    { _id: 10, name: "Sunshine State Rescue", location: "Orlando, FL" },
    { _id: 11, name: "Heartland Animal Center", location: "Kansas City, MO" },
    { _id: 12, name: "Bay Area Adoption", location: "San Francisco, CA" },
    { _id: 13, name: "Desert Paws Shelter", location: "Phoenix, AZ" },
    { _id: 14, name: "Great Lakes Rescue", location: "Detroit, MI" },
    { _id: 15, name: "Southern Comfort Pets", location: "Atlanta, GA" },
    { _id: 16, name: "Northeast Animal League", location: "Boston, MA" },
    { _id: 17, name: "Midwest Mutts", location: "Minneapolis, MN" },
    { _id: 18, name: "Pacific Northwest Paws", location: "Vancouver, WA" },
    { _id: 19, name: "Texas Pet Haven", location: "Dallas, TX" },
    { _id: 20, name: "Florida Feline Friends", location: "Tampa, FL" },
    { _id: 21, name: "California Canine Club", location: "Sacramento, CA" },
    { _id: 22, name: "Illinois Animal Aid", location: "Springfield, IL" },
    { _id: 23, name: "Ohio Pet Partners", location: "Columbus, OH" },
    { _id: 24, name: "Pennsylvania Paws", location: "Philadelphia, PA" },
    { _id: 25, name: "New Jersey New Beginnings", location: "Newark, NJ" }
  ]);

  await db.collection('pets').insertMany([
    { _id: 1, name: "Max", species: "Dog", breed: "Labrador Retriever", age: 2, description: "Friendly and energetic lab puppy", status: "available", imageUrl: "https://example.com/dog1.jpg", shelterId: 1, createdAt: new Date() },
    { _id: 2, name: "Bella", species: "Cat", breed: "Siamese", age: 1, description: "Elegant and affectionate Siamese cat", status: "available", imageUrl: "https://example.com/cat1.jpg", shelterId: 2, createdAt: new Date() },
    { _id: 3, name: "Charlie", species: "Dog", breed: "Golden Retriever", age: 3, description: "Loyal and playful golden retriever", status: "available", imageUrl: "https://example.com/dog2.jpg", shelterId: 1, createdAt: new Date() },
    { _id: 4, name: "Luna", species: "Cat", breed: "Persian", age: 4, description: "Calm and fluffy Persian cat", status: "available", imageUrl: "https://example.com/cat2.jpg", shelterId: 2, createdAt: new Date() },
    { _id: 5, name: "Rocky", species: "Dog", breed: "Bulldog", age: 5, description: "Strong and loving bulldog", status: "available", imageUrl: "https://example.com/dog3.jpg", shelterId: 3, createdAt: new Date() },
    { _id: 6, name: "Mittens", species: "Cat", breed: "Maine Coon", age: 2, description: "Large and gentle Maine Coon", status: "available", imageUrl: "https://example.com/cat3.jpg", shelterId: 4, createdAt: new Date() },
    { _id: 7, name: "Buddy", species: "Dog", breed: "Beagle", age: 1, description: "Curious and friendly beagle puppy", status: "available", imageUrl: "https://example.com/dog4.jpg", shelterId: 1, createdAt: new Date() },
    { _id: 8, name: "Whiskers", species: "Cat", breed: "Tabby", age: 3, description: "Playful tabby cat", status: "available", imageUrl: "https://example.com/cat4.jpg", shelterId: 2, createdAt: new Date() },
    { _id: 9, name: "Duke", species: "Dog", breed: "German Shepherd", age: 4, description: "Protective and intelligent shepherd", status: "available", imageUrl: "https://example.com/dog5.jpg", shelterId: 3, createdAt: new Date() },
    { _id: 10, name: "Sassy", species: "Cat", breed: "Bengal", age: 2, description: "Energetic Bengal cat", status: "available", imageUrl: "https://example.com/cat5.jpg", shelterId: 4, createdAt: new Date() },
    { _id: 11, name: "Rex", species: "Dog", breed: "Poodle", age: 6, description: "Smart and curly poodle", status: "available", imageUrl: "https://example.com/dog6.jpg", shelterId: 5, createdAt: new Date() },
    { _id: 12, name: "Tigger", species: "Cat", breed: "Orange Tabby", age: 1, description: "Adventurous orange tabby", status: "available", imageUrl: "https://example.com/cat6.jpg", shelterId: 1, createdAt: new Date() },
    { _id: 13, name: "Jack", species: "Dog", breed: "Jack Russell Terrier", age: 3, description: "Energetic terrier", status: "available", imageUrl: "https://example.com/dog7.jpg", shelterId: 2, createdAt: new Date() },
    { _id: 14, name: "Shadow", species: "Cat", breed: "Black Cat", age: 5, description: "Mysterious black cat", status: "available", imageUrl: "https://example.com/cat7.jpg", shelterId: 3, createdAt: new Date() },
    { _id: 15, name: "Bear", species: "Dog", breed: "Husky", age: 2, description: "Beautiful husky with blue eyes", status: "available", imageUrl: "https://example.com/dog8.jpg", shelterId: 4, createdAt: new Date() },
    { _id: 16, name: "Pepper", species: "Cat", breed: "Calico", age: 4, description: "Colorful calico cat", status: "available", imageUrl: "https://example.com/cat8.jpg", shelterId: 5, createdAt: new Date() },
    { _id: 17, name: "Finn", species: "Dog", breed: "Border Collie", age: 1, description: "Intelligent border collie", status: "available", imageUrl: "https://example.com/dog9.jpg", shelterId: 1, createdAt: new Date() },
    { _id: 18, name: "Jasper", species: "Cat", breed: "Russian Blue", age: 3, description: "Elegant Russian Blue", status: "available", imageUrl: "https://example.com/cat9.jpg", shelterId: 2, createdAt: new Date() },
    { _id: 19, name: "Ollie", species: "Dog", breed: "Corgi", age: 2, description: "Adorable corgi with short legs", status: "available", imageUrl: "https://example.com/dog10.jpg", shelterId: 3, createdAt: new Date() },
    { _id: 20, name: "Smokey", species: "Cat", breed: "Scottish Fold", age: 6, description: "Cute Scottish Fold with folded ears", status: "available", imageUrl: "https://example.com/cat10.jpg", shelterId: 4, createdAt: new Date() }
  ]);

  // Insert other collections as needed...

  console.log("Sample data inserted successfully into MongoDB!");
  return 'done.';
}

main()
  .then(console.log)
  .catch(console.error)
  .finally(() => client.close());
