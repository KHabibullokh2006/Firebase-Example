package com.example.firebaseexample

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.firebaseexample.model.User
import com.example.firebaseexample.ui.theme.FirebaseExampleTheme
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth

class SettingsActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FirebaseExampleTheme {
                // A surface container using the 'background' color from the theme
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
                    val shared = MyShared.getInstance(this)
                    var user = shared.getUser()

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ){
                        Text(text = user?.name.toString(), fontSize = 25.sp)
                        Button(onClick = {
                            mGoogleSignInClient.signOut()
                            shared.logOut()
                            val i = Intent(this@SettingsActivity, MainActivity::class.java)
                            startActivity(i)
                        }) {
                            Text(text = "Sign Out")
                        }
                    }
                }
            }
        }
    }
}

