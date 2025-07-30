package com.example.shoppinglistapp

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp


data class ShoppingItem(
    val id: Int,
    val name: String,
    val quantity: Int,
    val isEdited: Boolean = false
)

@Composable
fun ShoppingListApp() {
    var sItems by remember { mutableStateOf(listOf<ShoppingItem>()) }
    var showDialogBox by remember { mutableStateOf(false) }
    var itemName by remember { mutableStateOf("") }
    var itemQuantity by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center
    ) {
        Button(
            onClick = { showDialogBox = true },
            modifier = Modifier.align(Alignment.CenterHorizontally),
            colors = ButtonDefaults.buttonColors(Color(0xFFFFA500))
        ) {
            Text("Add Item",
                fontWeight = FontWeight.ExtraBold,
                color = Color.White)
        }

        // ✅ Pass state to AlertDialogBox
        AlertDialogBox(
            showDialogBox = showDialogBox,
            onDismiss = { showDialogBox = false },
            itemName = itemName,
            itemQuantity = itemQuantity,
            onItemNameChange = { itemName = it },
            onItemQuantityChange = { itemQuantity = it },
            onAddItem = {
                val quantityInt = itemQuantity.toIntOrNull()
                if (itemName.isNotBlank() && quantityInt != null) {
                    val newItem = ShoppingItem(
                        id = (sItems.maxOfOrNull { it.id } ?: 0) + 1,
                        name = itemName,
                        quantity = quantityInt
                    )
                    sItems = sItems + newItem
                    showDialogBox = false
                    itemName = ""
                    itemQuantity = ""
                }
            }
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            items(sItems) { item ->
                if (item.isEdited) {
                    ShoppingListEditor(
                        item = item,
                        onEditComplete = { editedName, editedQuantity ->
                            sItems = sItems.map {
                                if (it.id == item.id) it.copy(
                                    name = editedName,
                                    quantity = editedQuantity,
                                    isEdited = false
                                ) else it.copy(isEdited = false)
                            }
                        }
                    )
                } else {
                    ShoppingListItem(
                        item = item,
                        onEditClick = {
                            sItems = sItems.map { it.copy(isEdited = it.id == item.id) }
                        },
                        onDeleteClick = { sItems = sItems - item }
                    )
                }
            }
        }
    }
}

@Composable
fun ShoppingListItem(
    item: ShoppingItem,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .border(border = BorderStroke(1.dp, Color(0xFFFFA500)), RoundedCornerShape(10)),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = item.name,
            modifier = Modifier
                .weight(1f)
                .padding(8.dp)
        )
        Text(
            text = "Qty: ${item.quantity}",
            modifier = Modifier
                .padding(8.dp)
                .weight(1f)
        )
        Row {
            IconButton(onClick = onEditClick) {
                Icon(Icons.Default.Edit, contentDescription = "Edit")
            }
            IconButton(onClick = onDeleteClick) {
                Icon(Icons.Default.Delete, contentDescription = "Delete")
            }
        }
    }
}

@Composable
fun ShoppingListEditor(
    item: ShoppingItem,
    onEditComplete: (String, Int) -> Unit
) {
    var editedName by remember { mutableStateOf(item.name) }
    var editedQuantity by remember { mutableStateOf(item.quantity.toString()) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(color = Color.White),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Column {
            BasicTextField(
                value = editedName,
                onValueChange = { editedName = it },
                modifier = Modifier
                    .wrapContentSize()
                    .padding(8.dp),
                singleLine = true
            )
            BasicTextField(
                value = editedQuantity,
                onValueChange = { editedQuantity = it },
                modifier = Modifier
                    .wrapContentSize()
                    .padding(8.dp),
                singleLine = true
            )
        }
        Button(
            onClick = { onEditComplete(editedName, editedQuantity.toIntOrNull() ?: 1) },
            colors = ButtonDefaults.buttonColors(Color(0xFFFFA500)),
            modifier = Modifier.align(Alignment.CenterVertically)
            )
        {
            Text("Save")
        }
    }
}

// REFACTORED: Takes state + callbacks from parent
@Composable
fun AlertDialogBox(
    showDialogBox: Boolean,
    onDismiss: () -> Unit,
    itemName: String,
    itemQuantity: String,
    onItemNameChange: (String) -> Unit,
    onItemQuantityChange: (String) -> Unit,
    onAddItem: () -> Unit
) {
    if (showDialogBox) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Add Shopping Item") },
            text = {
                Column {
                    OutlinedTextField(
                        value = itemName,
                        onValueChange = onItemNameChange,
                        singleLine = true,
                        label = { Text("Item Name") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Gray,
                            unfocusedBorderColor = Color.DarkGray,
                            focusedLabelColor = Color.LightGray,
                            cursorColor = Color.White,
                            unfocusedLabelColor = Color.Gray
                        )
                    )
                    OutlinedTextField(
                        value = itemQuantity,
                        onValueChange = onItemQuantityChange,
                        singleLine = true,
                        label = { Text("Quantity") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Gray,
                            unfocusedBorderColor = Color.DarkGray,
                            focusedLabelColor = Color.LightGray,
                            cursorColor = Color.White,
                            unfocusedLabelColor = Color.Gray
                        )
                    )
                }
            },
            confirmButton = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = onAddItem,
                        colors = ButtonDefaults.buttonColors(Color(0xFFFFA500))
                    ) {
                        Text("Add")
                    }
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(Color(0xFFFFA500))
                    ) {
                        Text("Cancel")
                    }
                }
            }
        )
    }
}
