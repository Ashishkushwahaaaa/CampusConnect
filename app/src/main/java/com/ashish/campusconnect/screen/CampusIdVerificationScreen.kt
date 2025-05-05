package com.ashish.campusconnect.screen

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch


@Composable
fun CampusIdVerificationScreen(
    onVerified: (String, String) -> Unit,
    onGuestContinue: () -> Unit
) {
    var campusId by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf("student") }
    var isVerified by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(onClick = onGuestContinue) {
                Text("Continue as Guest")
            }
        }

        Text(
            text = "Enter Campus ID",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        OutlinedTextField(
            value = campusId,
            onValueChange = { campusId = it },
            label = { Text("Campus ID") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text("Select Your Role", style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            RoleButton("student", selectedRole) { selectedRole = "student" }
            RoleButton("admin", selectedRole) { selectedRole = "admin" }
            RoleButton("faculty", selectedRole) { selectedRole = "faculty" }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                scope.launch {
                    val db = FirebaseFirestore.getInstance()
                    val ref = db.collection("campus_ids").document(selectedRole)
                    ref.get().addOnSuccessListener { document ->
                        val ids = document.get("ids") as? List<*>
                        if (ids?.contains(campusId) == true) {
                            db.collection("users")
                                .whereEqualTo("campusId", campusId)
                                .get()
                                .addOnSuccessListener { snapshot ->
                                    if (snapshot.isEmpty) {
                                        isVerified = true
                                        onVerified(selectedRole, campusId)
                                    } else {
                                        Toast.makeText(context, "ID already used.", Toast.LENGTH_SHORT).show()
                                    }
                                }
                        } else {
                            Toast.makeText(context, "Campus ID not found for selected role.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Verify Campus ID")
        }

        if (isVerified) {
            Spacer(modifier = Modifier.height(16.dp))
            Text("✅ ID Verified. You may proceed.", color = Color.Green)
        }
    }
}

@Composable
fun RoleButton(role: String, selected: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (role == selected) MaterialTheme.colorScheme.primary
            else Color.LightGray,
            contentColor = if (role == selected) Color.White else Color.Black
        )
    ) {
        Text(role.capitalize())
    }
}


@Preview(showBackground = true)
@Composable
fun CampusIdVerificationScreenPreview() {
    CampusIdVerificationScreen({ _, _ -> }, {})
}