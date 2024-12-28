const admin = require("../dbAuth");

const createUser = (req, res) => {
  const { email, password } = req.body;

  admin
    .auth()
    .createUser({ email, password })
    .then((userRecord) => {
      // Add the user to the Realtime Database
      const db = admin.database();
      const usersRef = db.ref("users");
      usersRef.child(userRecord.uid).set({ email });
      console.log("Successfully created new user:", userRecord.uid);
      res.status(200).json({ success: true, userId: userRecord.uid });
    })
    .catch((error) => {
      console.error("Error creating user:", error);
      res.status(500).json({ success: false, error: error.message });
    });
};

const updateUser = (req, res) => {
  const { userId, role = "Client" } = req.body;

  const db = admin.database();
  const usersRef = db.ref("users");
  usersRef
    .child(userId)
    .update({ role })
    .then(() => {
      console.log("User updated successfully");
      res.status(200).json({ success: true });
    })
    .catch((error) => {
      console.error("Error updating user:", error);
      res.status(500).json({ success: false, error: error.message });
    });
};

const getAllUsers = (req, res) => {
  const db = admin.database();
  const usersRef = db.ref("users");
  usersRef
    .once("value")
    .then((snapshot) => {
      const users = snapshot.val();
      res.status(200).json({ success: true, users });
    })
    .catch((error) => {
      console.error("Error getting users:", error);
      res.status(500).json({ success: false, error: error.message });
    });
};

const deleteUser = (req, res) => {
  const { userId } = req.body;

  admin
    .auth()
    .deleteUser(userId)
    .then(() => {
      // Remove the user from the Realtime Database
      const db = admin.database();
      const usersRef = db.ref("users");
      usersRef.child(userId).remove();
      console.log("User deleted successfully");
      res.status(200).json({ success: true });
    })
    .catch((error) => {
      console.error("Error deleting user:", error);
      res.status(500).json({ success: false, error: error.message });
    });
};

module.exports = {
  createUser,
  deleteUser,
  getAllUsers,
  updateUser,
};
