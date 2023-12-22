package com.example.firebaseexample

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ExitToApp
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.firebaseexample.model.User
import com.example.firebaseexample.ui.theme.FirebaseExampleTheme
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ContactActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FirebaseExampleTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    val uid = intent.getStringExtra("uid")
                    Log.d("UID", uid.toString())


                    val userList = remember {
                        mutableStateListOf(User())
                    }

                    val reference = Firebase.database.reference.child("contacts")

                    reference.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            userList.clear()
                            val users = snapshot.children
                            users.forEach {
                                val user = it.getValue(User::class.java)
                                Log.d("TAG-C1",user?.name.toString())
                                if (user != null && user.uid != uid) {
                                    Log.d("TAG-C2",user.name.toString())
                                    userList.add(user)
                                }
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Log.d("TAG", "onCancelled: ${error.details}")
                        }
                    })

                    Column(Modifier.fillMaxSize().padding(10.dp).fillMaxHeight()) {
                        LazyColumn(Modifier.padding(8.dp)) {
                            items(userList) {
                                Row(
                                    Modifier
                                        .fillMaxWidth(9f)
                                        .padding(10.dp)
                                        .height(50.dp)
                                        .clickable {
                                            val i = Intent(
                                                this@ContactActivity,
                                                ChatActivity::class.java
                                            )
                                            i.putExtra("uid", uid)
                                            i.putExtra("user",it)
                                            i.putExtra("useruid", it.uid)
                                            startActivity(i)
                                        },
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    AsyncImage(
                                        model = ImageRequest.Builder(LocalContext.current)
                                            .data(it.photo)
                                            .crossfade(true)
                                            .build(),
                                        placeholder = painterResource(R.drawable.send),
                                        contentDescription = ("no image"),
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.clip(CircleShape)
                                    )
                                    Text(
                                        text = it.name ?: "",
                                        Modifier.padding(start = 12.dp),
                                        fontSize = 22.sp
                                    )
                                }
                            }
                        }
                    }


                }
            }
        }
    }
    @Composable
    private fun ContactTopBar() {
        Row(
            Modifier
                .height(60.dp)
                .background(color = Color.Blue)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick={

                },
                modifier = Modifier
                    .clip(CircleShape)
                    .size(60.dp)
            ){
                Icon(Icons.Rounded.Settings, contentDescription = "")
            }
            Text(text = "Telegram", color = Color.White, fontSize = 25.sp)
            Button(onClick = {

            }, Modifier.size(60.dp)) {
                Icon(Icons.Rounded.ExitToApp, contentDescription = "")
            }
        }
    }

}

