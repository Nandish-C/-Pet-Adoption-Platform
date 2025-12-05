const { MongoClient } = require('mongodb');

const uri = 'mongodb://localhost:27017';

const client = new MongoClient(uri);

async function dropDatabase() {
  try {
    await client.connect();
    const db = client.db('petadoptiondb');
    await db.dropDatabase();
    console.log('Database petadoptiondb dropped successfully! Old state restored.');
  } catch (error) {
    console.error('Error dropping database:', error);
  } finally {
    await client.close();
  }
}

dropDatabase();
