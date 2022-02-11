// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import java.sql.DriverManager

@ExperimentalUnitApi
fun main() = application {
    val connection = DriverManager.getConnection("jdbc:sqlite:src/main/kotlin/Main/NoteItems.db")
    val statement = connection.createStatement()
    val result = statement.executeQuery("SELECT * FROM NoteTable")

    Window(
        onCloseRequest = ::exitApplication,
        title = "Note"
    ) {
        MaterialTheme {
            Row {
                val notes = remember {
                    val list = mutableStateListOf<Note>()

                    while (result.next()) {
                        val id = result.getInt("NoteID")
                        val data = result.getString("data")


                        list.add(
                            Note(
                                noteID = id,
                                data = data
                            )
                        )
                    }
                    list
                }
                val clickedIndex = remember { mutableStateOf(0) }
                val noteState = remember { mutableStateOf("") }
                val noteErrorState = remember { mutableStateOf(false) }

                LazyColumn(
                    modifier = Modifier
                        .weight(weight = 0.4f)
                        .background(color = Color.LightGray)
                        .fillMaxHeight()
                ) {
                    itemsIndexed(notes) { index, note ->
                        val thisNoteIsSelected: Boolean = clickedIndex.value == index
                        NoteItem(
                            data = note.data,
                            deleteNote = {
                                statement.executeUpdate("DELET FROM NoteTable WHERE NoteID='${note.noteID}'")
                                notes.remove(note)
                            },
                            onNoteClicked = {
                                noteState.value = note.data
                                clickedIndex.value = notes.indexOf(note)
                                noteErrorState.value = false
                            },
                            isSelected = thisNoteIsSelected

                        )
                    }
                }

                Box(
                    modifier = Modifier.fillMaxHeight()
                        .weight(0.6f)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.align(Alignment.TopCenter)
                            .fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = noteState.value,
                            onValueChange = {
                                noteState.value = it
                            },
                            label = {
                                Text("Note")
                            },
                            placeholder = {
                                Text("Enter note...")
                            },
                            colors = TextFieldDefaults.outlinedTextFieldColors(),
                            modifier = Modifier
                                .padding(8.dp)
                                .fillMaxWidth()
                                .fillMaxHeight(0.7f),
                            isError = noteErrorState.value
                        )
                        if (noteErrorState.value) {
                            Text(
                                text = "Note is empty !",
                                color = MaterialTheme.colors.error,
                                style = MaterialTheme.typography.caption,
                                modifier = Modifier
                                    .padding(horizontal = 8.dp)
                                    .align(alignment = Alignment.Start)
                            )
                        }

                        Button(onClick = {
                            if (noteState.value == "") {
                                noteErrorState.value = true
                            } else {
                                val clickedNote = notes[clickedIndex.value]
                                val newNote = clickedNote.copy(data = noteState.value)
                                notes[clickedIndex.value] = newNote
                                statement.executeUpdate(
                                    "UPDATE NoteTable SET " +
                                            "NoteID='${newNote.noteID}', " +
                                            "data='${newNote.data}' " +
                                            "WHERE NoteID='${clickedNote.noteID}'"
                                )
                            }
                        }
                        ) {
                            Text("Save")
                        }
                    }


                    FloatingActionButton(
                        onClick = {
                            noteState.value = ""
                            val element = Note(data = "", noteID = null)
                            notes.add(element)
                            clickedIndex.value = notes.indexOf(element)
                            noteErrorState.value = false
                        },
                        modifier = Modifier
                            .align(alignment = Alignment.BottomEnd)
                            .padding(end = 16.dp, bottom = 16.dp),
                        backgroundColor = Color(0xffff0078),
                        contentColor = Color.White
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null)
                    }
                }
            }
        }
    }
}

@Composable
fun NoteItem(data: String, deleteNote: () -> Unit, onNoteClicked: () -> Unit, isSelected: Boolean) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = if (isSelected)
            Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(color = Color.Gray)
                .clickable { onNoteClicked() }
                .padding(8.dp)
                .fillMaxWidth()
        else
            Modifier
                .clip(RoundedCornerShape(8.dp))
                .clickable { onNoteClicked() }
                .padding(8.dp)
                .fillMaxWidth()
    ) {
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = data,
            color = Color.Black,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            modifier = Modifier.weight(1f),
            overflow = TextOverflow.Ellipsis

        )
        IconButton(onClick = {
            deleteNote()
        }) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = null
            )
        }

    }
}
