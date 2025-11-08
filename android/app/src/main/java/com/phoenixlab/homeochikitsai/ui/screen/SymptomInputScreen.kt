package com.homeopathy.ai.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.phoenixlab.homeochikitsai.data.model.Suggestion
import com.phoenixlab.homeochikitsai.data.model.SymptomRequest
import com.phoenixlab.homeochikitsai.data.remote.RetrofitInstance

import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SymptomInputScreen(modifier: Modifier = Modifier) {
    var text by remember { mutableStateOf("")
    }
    var suggestions by remember { mutableStateOf<List<Suggestion>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Homeopathy AI Doctor") })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                label = { Text("Describe your symptoms") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = false,
                maxLines = 4
            )

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = {
                    scope.launch {
                        isLoading = true
                        try {
                            val response = RetrofitInstance.api.sendSymptom(
                                SymptomRequest(
                                    user_id = "user1",
                                    text = text
                                )
                            )
                            suggestions = response.suggested
                        } catch (e: Exception) {
                            e.printStackTrace()
                        } finally {
                            isLoading = false
                        }
                    }
                },
                enabled = text.isNotBlank() && !isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (isLoading) "Analyzing..." else "Get Suggestion")
            }

            Spacer(Modifier.height(24.dp))

            if (suggestions.isNotEmpty()) {
                Text("Recommended Remedies:", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
            }

            suggestions.forEach { suggestion ->
                RemedyCard(suggestion)
            }
        }
    }
}

@Composable
fun RemedyCard(suggestion: Suggestion) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("💊 ${suggestion.name}", style = MaterialTheme.typography.titleMedium)
            Text("Potency: ${suggestion.potency}")
            Text("Dosage: ${suggestion.dosage}")
            Text("Confidence: ${suggestion.confidence}%")
            Text("Reason: ${suggestion.rationale}")
        }
    }
}
