package com.example.firebaseexample

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.firebaseexample.model.User
import com.example.firebaseexample.ui.theme.FirebaseExampleTheme
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MainActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth
    val database = Firebase.database
    val myRef = FirebaseDatabase.getInstance().reference.push().child("contacts")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FirebaseExampleTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    auth = FirebaseAuth.getInstance()
                    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.client_id))
                        .requestEmail()
                        .build()
                    val mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Button(onClick = {
                            val signInIntent = mGoogleSignInClient.signInIntent
                            startActivityForResult(signInIntent, 1)
                        }) {
                            Text(text = "Sign In with Google")
                        }
                    }
                }
            }
        }
    }


    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account.idToken)
            } catch (e: ApiException) {
                Log.d("TAG", "error: ${e.message}")
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String?) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        val shared = MyShared.getInstance(this)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
//                    Log.d("User", user?.displayName.toString())
                    val userData = User(user?.displayName, user?.uid, user?.photoUrl.toString())
                    var b = true

                    myRef.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val children = snapshot.children
                            children.forEach {
                                val u = it.getValue(User::class.java)
                                if (u != null && u.uid == userData.uid) {
                                    b = false
                                }
                            }
                            if (b) {
                                setUser(userData,shared)
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Log.d("TAG", "Database error: ${error.message}")
                        }
                    })
                    shared.setUser(userData)
                    val i = Intent(this, ContactActivity::class.java)
                    i.putExtra("uid", userData.uid)
                    startActivity(i)

                } else {
                    Log.d("TAG", "firebaseAuthWithGoogle: Task Unsuccessful")
                }
            }
    }

    private fun setUser(userData: User, shared: MyShared) {
        myRef.child(userData.uid?: "").setValue(userData)
            .addOnSuccessListener {
                shared.setUser(userData)
                val i = Intent(this, ContactActivity::class.java)
                i.putExtra("uid", userData.uid)
                startActivity(i)
            }
    }
}
