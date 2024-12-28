const express = require("express");
const bodyParser = require("body-parser");
const cors = require("cors");
const {
  createUser,
  deleteUser,
  getAllUsers,
  updateUser,
} = require("./userActions");

const app = express();
const port = 3000;

// Initialize Firebase Admin SDK
// admin.initializeApp({
//   credential: admin.credential.cert(serviceAccount),
//   databaseURL: "https://fir-setup-8e316.firebaseio.com",
// });

app.use(bodyParser.json());
app.use(cors()); // Enable CORS

app.post("/deleteUser", deleteUser);
app.post("/createUser", createUser);
app.post("/updateUser", updateUser);
app.get("/getAllUsers", getAllUsers);

// Start the server
app.listen(port, "0.0.0.0", () => {
  console.log(`Server is running on port ${port}`);
});
