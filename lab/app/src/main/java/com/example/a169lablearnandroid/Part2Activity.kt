package com.example.a169lablearnandroid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.a169lablearnandroid.ui.theme._169LabLearnAndroidTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class Part2Activity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            _169LabLearnAndroidTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ContactListScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

class ContactsViewModel : ViewModel() {
    private val _contacts = MutableStateFlow<List<String>>(emptyList())
    val contacts: StateFlow<List<String>> = _contacts

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val alphabet = ('A'..'Z').toList()
    private var idCounter = 0

    init {
        loadMore()
    }

    fun loadMore() {
        if (_isLoading.value) return
        viewModelScope.launch {
            _isLoading.value = true
            delay(2000) // Mock loading time

            // Generate 15 random contacts
            val newContacts = List(15) {
                val letter = alphabet.random()
                "$letter - Person ${++idCounter}"
            }

            // Combine and sort them alphabetically so they slot into the correct sticky headers
            _contacts.value = (_contacts.value + newContacts).sorted()
            _isLoading.value = false
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ContactListScreen(
    modifier: Modifier = Modifier,
    viewModel: ContactsViewModel = viewModel()
) {
    val contacts by viewModel.contacts.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // Group the contacts by their starting letter
    val groupedContacts = contacts.groupBy { it.firstOrNull()?.uppercase() ?: "" }

    LazyColumn(modifier = modifier.fillMaxSize()) {
        groupedContacts.forEach { (initial, contactsForInitial) ->
            stickyHeader {
                Text(
                    text = initial,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            items(items = contactsForInitial, key = { it }) { contact ->
                Text(
                    text = contact,
                    modifier = Modifier
                        .animateItem() // Smooth animation when item is added/moved
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                )
            }
        }

        // Pagination Trigger & Loading Indicator at the end of the list
        item {
            if (contacts.isNotEmpty() && !isLoading) {
                LaunchedEffect(Unit) {
                    viewModel.loadMore()
                }
            }

            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ContactListPreview() {
    _169LabLearnAndroidTheme {
        ContactListScreen()
    }
}