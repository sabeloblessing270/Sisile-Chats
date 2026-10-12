package com.sisile.chats

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import org.json.JSONArray
import org.json.JSONObject

private val Purple = Color(0xFF9B5DE5)
private val Dark = Color(0xFF100D18)
private val Panel = Color(0xFF201A2B)
private val Soft = Color(0xFFBDA8D8)

data class ChatMessage(val text: String, val mine: Boolean, val time: String)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { SisileApp() }
    }
}

@Composable
fun SisileApp() {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("sisile_local", 0) }
    var screen by remember { mutableStateOf("Chats") }
    var contact by remember { mutableStateOf("My offline chat") }
    var draft by remember { mutableStateOf("") }
    var messages by remember {
        mutableStateOf(loadMessages(prefs.getString("messages", "[]") ?: "[]"))
    }
    fun persist(list: List<ChatMessage>) {
        val arr = JSONArray()
        list.forEach { m ->
            arr.put(JSONObject().put("text", m.text).put("mine", m.mine).put("time", m.time))
        }
        prefs.edit().putString("messages", arr.toString()).apply()
    }
    MaterialTheme(colorScheme = darkColorScheme(
        primary = Purple, secondary = Soft, background = Dark, surface = Panel,
        onPrimary = Color.White, onBackground = Color.White, onSurface = Color.White
    )) {
        Scaffold(
            containerColor = Dark,
            topBar = {
                Row(
                    Modifier.fillMaxWidth().background(Panel).padding(horizontal = 18.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(Modifier.size(44.dp).background(Purple, CircleShape), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Forum, contentDescription = null, tint = Color.White)
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text("Sisile Chats", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Text("Offline-first • messages saved on this phone", color = Soft, style = MaterialTheme.typography.bodySmall)
                    }
                    IconButton(onClick = { screen = "About" }) { Icon(Icons.Default.Info, contentDescription = "About") }
                }
            },
            bottomBar = {
                NavigationBar(containerColor = Panel, contentColor = Soft) {
                    listOf("Chats", "Share", "About").forEach { item ->
                        NavigationBarItem(
                            selected = screen == item,
                            onClick = {
                                screen = item
                                if (item == "Share") {
                                    val text = messages.filter { it.mine }.joinToString("\n") { it.text }
                                    val intent = Intent(Intent.ACTION_SEND).apply {
                                        type = "text/plain"
                                        putExtra(Intent.EXTRA_SUBJECT, "Sisile Chats message")
                                        putExtra(Intent.EXTRA_TEXT, text.ifBlank { "Hello from Sisile Chats!" })
                                    }
                                    context.startActivity(Intent.createChooser(intent, "Share using Bluetooth, Nearby Share, or another app"))
                                }
                            },
                            icon = { Icon(when(item) {
                                "Chats" -> Icons.Default.Chat
                                "Share" -> Icons.Default.Bluetooth
                                else -> Icons.Default.Settings
                            }, contentDescription = item) },
                            label = { Text(item) }
                        )
                    }
                }
            }
        ) { padding ->
            when (screen) {
                "Chats" -> Column(Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
                    Card(colors = CardDefaults.cardColors(containerColor = Panel), shape = RoundedCornerShape(16.dp)) {
                        Row(Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.WifiOff, contentDescription = null, tint = Purple, modifier = Modifier.size(28.dp))
                            Spacer(Modifier.width(10.dp))
                            Column {
                                Text("No mobile data needed to read or save messages", fontWeight = FontWeight.SemiBold)
                                Text("Nearby sharing uses your phone's available sharing options.", color = Soft, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                    Spacer(Modifier.height(14.dp))
                    Text("LOCAL CONVERSATION", color = Soft, style = MaterialTheme.typography.labelMedium)
                    Spacer(Modifier.height(8.dp))
                    LazyColumn(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        if (messages.isEmpty()) {
                            item {
                                Box(Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(Icons.Default.MarkChatUnread, contentDescription = null, tint = Purple, modifier = Modifier.size(44.dp))
                                        Spacer(Modifier.height(8.dp))
                                        Text("Your conversation starts here")
                                        Text("Write a message below. It stays on this phone.", color = Soft, style = MaterialTheme.typography.bodySmall)
                                    }
                                }
                            }
                        }
                        items(messages) { msg ->
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = if (msg.mine) Arrangement.End else Arrangement.Start) {
                                Column(
                                    Modifier.widthIn(max = 300.dp).background(if (msg.mine) Color(0xFF58358A) else Panel, RoundedCornerShape(16.dp)).padding(12.dp)
                                ) {
                                    Text(msg.text)
                                    Text(msg.time, color = Color(0xFFD9C9ED), style = MaterialTheme.typography.labelSmall, modifier = Modifier.align(Alignment.End))
                                }
                            }
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            value = draft, onValueChange = { draft = it },
                            modifier = Modifier.weight(1f),
                            placeholder = { Text("Write a local message…") },
                            shape = RoundedCornerShape(24.dp),
                            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                            maxLines = 4
                        )
                        Spacer(Modifier.width(8.dp))
                        FloatingActionButton(
                            onClick = {
                                val value = draft.trim()
                                if (value.isNotEmpty()) {
                                    val time = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault()).format(java.util.Date())
                                    val updated = messages + ChatMessage(value, true, time)
                                    messages = updated
                                    persist(updated)
                                    draft = ""
                                }
                            },
                            containerColor = Purple, contentColor = Color.White
                        ) { Icon(Icons.Default.Send, contentDescription = "Save message") }
                    }
                }
                "About" -> Column(Modifier.fillMaxSize().padding(padding).padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("About Sisile Chats", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    Text("This is the first offline-first prototype. It stores your local messages on this phone and can open Android's share sheet so you can choose Bluetooth or another sharing method.")
                    Text("Important limitations", color = Purple, fontWeight = FontWeight.Bold)
                    Text("• This prototype does not yet deliver live messages automatically to another Sisile Chats user.")
                    Text("• Internet sync, phone-number login, groups, voice/video calls, and end-to-end encryption are not implemented yet.")
                    Text("• Sharing a message through Android is not the same as encrypted in-app messaging.")
                    Text("• Messages remain in this app's local storage until you clear app data or uninstall it.")
                }
                else -> Column(Modifier.fillMaxSize().padding(padding).padding(20.dp)) {
                    Text("Share from Sisile Chats", style = MaterialTheme.typography.headlineSmall)
                    Text("Choose Bluetooth or a nearby sharing option from Android's share menu to send a text manually.")
                }
            }
        }
    }
}

private fun loadMessages(raw: String): List<ChatMessage> {
    return try {
        val arr = JSONArray(raw)
        (0 until arr.length()).map { i ->
            val obj = arr.getJSONObject(i)
            ChatMessage(obj.optString("text"), obj.optBoolean("mine"), obj.optString("time"))
        }
    } catch (_: Exception) { emptyList() }
}
