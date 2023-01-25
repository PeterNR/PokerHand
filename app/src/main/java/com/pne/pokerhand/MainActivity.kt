package com.pne.pokerhand

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.pne.pokerhand.ui.theme.PokerHandTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PokerHandTheme {
                val pokerViewModel = hiltViewModel<PokerViewModel>()
                val state = pokerViewModel.state

                // A surface container using the 'background' color from the theme
                Column(
                    modifier = Modifier
                        .padding(10.dp)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.Center,
                ) {
                    Hand(pokerViewModel)
                }
                if (state.selectorIsOpen) {
                    AlertSelector(pokerViewModel, state)
                }
            }
        }
    }
}

@Composable
fun Hand(
    viewModel: PokerViewModel = hiltViewModel(),
) {
    val state = viewModel.state

    Surface(
        modifier = Modifier
            .height(300.dp)
            .fillMaxWidth(),
        elevation = 4.dp,
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            if (state.hand.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    state.hand.forEach {
                        Surface(
                            elevation = 4.dp,
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(50.dp)
                                    .border(1.dp, Color.Black, RoundedCornerShape(3.dp)),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    textAlign = TextAlign.Center,
                                    text = "${viewModel.ShowVisualValue(it.value)} " +
                                            "${it.type.toString().first().lowercaseChar()}"
                                )
                            }
                        }
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .height(50.dp)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = "Press \"Shuffle\" to hand out random of cards." +
                                "\nPress \"Cheat\" to manually select cards.",
                        textAlign = TextAlign.Center
                    )
                }
            }
            Text(text = state.description)
            Row {
                Button(
                    modifier = Modifier
                        .height(50.dp)
                        .weight(1f),
                    onClick = { viewModel.ShuffleNewHand(null) }
                ) {
                    Text(text = "Shuffle")
                }

                Spacer(modifier = Modifier.width(10.dp))

                Button(
                    modifier = Modifier
                        .height(50.dp)
                        .weight(1f),
                    onClick = {
                        viewModel.ToggleSelector()
                        viewModel.ShuffleNewHand(state.selectedHand)
                        viewModel.EmptySelectedHand()
                    }
                ) {
                    Text(text = "Cheat")
                }

            }
        }
    }
}

@Composable
fun AlertSelector(viewModel: PokerViewModel, state: PokerState) {
    var valueIsOpen by remember { mutableStateOf(false) }
    var selectedValueIndex by remember { mutableStateOf(0) }
    val valueList = mutableListOf<String>()
    var typeIsOpen by remember { mutableStateOf(false) }
    var selectedTypeIndex by remember { mutableStateOf(0) }
    val typeList = mutableListOf<CardType>()

    repeat(13) { valueList.add(viewModel.ShowVisualValue(it + 1)) }
    CardType.values().forEach { typeList.add(it) }

    Dialog(onDismissRequest = { viewModel.ToggleSelector() }) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
        ) {
            Column(
                modifier = Modifier.padding(10.dp),
                verticalArrangement = Arrangement.SpaceAround
            ) {
                Text(text = "Select 5 cards. \n${state.selectedHand.size} cards are selected.")
                Row {
                    Box(modifier = Modifier.weight(1f)) {
                        CustomDropdownMenu(
                            isOpen = valueIsOpen,
                            selectedIndex = selectedValueIndex,
                            itemList = valueList,
                            toggleOpen = { valueIsOpen = !valueIsOpen },
                            changeIndex = { index -> selectedValueIndex = index },
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Box(modifier = Modifier.weight(1f)) {
                        CustomDropdownMenu(
                            isOpen = typeIsOpen,
                            selectedIndex = selectedTypeIndex,
                            itemList = typeList,
                            toggleOpen = { typeIsOpen = !typeIsOpen },
                            changeIndex = { index -> selectedTypeIndex = index },
                        )
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    if (state.selectedHand.size == 5) {
                        Button(onClick = {
                            viewModel.ToggleSelector()
                            viewModel.ShuffleNewHand(state.selectedHand)
                        }) {
                            Text(text = "Done")
                        }
                    } else {
                        Button(onClick = {
                            viewModel.AddToSelectedHand(
                                Card(
                                    typeList[selectedTypeIndex],
                                    selectedValueIndex + 1
                                )
                            )
                        }) {
                            Text(text = "Add card")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CustomDropdownMenu(
    isOpen: Boolean,
    selectedIndex: Int,
    itemList: List<Any>,
    toggleOpen: () -> Unit,
    changeIndex: (Int) -> Unit,
) {
    Text(
        text = itemList[selectedIndex].toString(),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { toggleOpen() }
            .background(Color.LightGray),
        textAlign = TextAlign.Center
    )
    DropdownMenu(
        modifier = Modifier
            .background(Color.LightGray),
        expanded = isOpen,
        onDismissRequest = { toggleOpen() }) {
        itemList.forEachIndexed { index, s ->
            DropdownMenuItem(onClick = {
                changeIndex(index)
                toggleOpen()
            }) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = s.toString(),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    PokerHandTheme {
//        Hand()
    }
}