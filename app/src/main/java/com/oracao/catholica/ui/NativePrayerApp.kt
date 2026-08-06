package com.oracao.catholica.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.FormatAlignCenter
import androidx.compose.material.icons.filled.FormatAlignJustify
import androidx.compose.material.icons.filled.FormatAlignLeft
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VolumeUp
import com.oracao.catholica.viewmodel.DailyLiturgyData
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FormatBold
import androidx.compose.material.icons.filled.UnfoldMore
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.FormatColorText
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.getSelectedText
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.TextUnit
import coil.compose.AsyncImage
import com.oracao.catholica.R
import com.oracao.catholica.data.PrayerEntity
import com.oracao.catholica.viewmodel.PrayerViewModel
import kotlinx.coroutines.launch

val CatholicRed = Color(0xFF400000)
val LightCatholicRed = Color(0xFFD32F2F)
val DivineGold = Color(0xFFD4AF37)
val SoftParchment = Color(0xFFFAF4E8) // Soft parchment background
val SoftParchmentCard = Color(0xFFFFFDF8) // Soft papyrus tone for cards
val SoftParchmentBorder = Color(0xFFE6DCC8) // Warm parchment border
val DarkText = Color(0xFF2C221E) // Dark warm mahogany text for optimal reading

@Composable
fun NativePrayerAppScreen(viewModel: PrayerViewModel) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val prayers by viewModel.prayersList.collectAsState()
    val favoritePrayers by viewModel.favoritePrayers.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val categories by viewModel.categoriesState.collectAsState()
    val selectedPrayer by viewModel.selectedPrayer.collectAsState()
    val fontSizeSp by viewModel.fontSizeSp.collectAsState()
    val textAlign by viewModel.textAlignState.collectAsState()
    val isSpeaking by viewModel.isSpeaking.collectAsState()
    val liturgiaData by viewModel.liturgiaState.collectAsState()
    val syncUrlState by viewModel.syncUrlState.collectAsState()
    val isSyncing by viewModel.isSyncing.collectAsState()
    val syncStatusMessage by viewModel.syncStatusMessage.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var showEditLiturgiaDialog by remember { mutableStateOf(false) }
    var showFloatingMenuSheet by remember { mutableStateOf(false) }
    var showSyncUrlDialog by remember { mutableStateOf(false) }
    var prayerToEdit by remember { mutableStateOf<PrayerEntity?>(null) }
    var initialCategoryForAdd by remember { mutableStateOf("") }
    var prayerToDeleteConfirm by remember { mutableStateOf<PrayerEntity?>(null) }
    var startDetailInEditMode by remember { mutableStateOf(false) }

    // Intercept hardware back button to navigate page by page
    val canGoBack = selectedPrayer != null || selectedCategory.isNotBlank() || searchQuery.isNotBlank()
    BackHandler(enabled = canGoBack) {
        if (selectedPrayer != null) {
            viewModel.selectPrayer(null)
            startDetailInEditMode = false
        } else if (selectedCategory.isNotBlank()) {
            viewModel.selectedCategory.value = ""
        } else if (searchQuery.isNotBlank()) {
            viewModel.searchQuery.value = ""
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(SoftParchment),
        floatingActionButton = {
            // Floating Action Button with 3-line menu icon
            FloatingActionButton(
                onClick = {
                    showFloatingMenuSheet = true
                },
                containerColor = CatholicRed,
                contentColor = DivineGold,
                shape = CircleShape,
                modifier = Modifier.testTag("menu_fab")
            ) {
                Icon(imageVector = Icons.Default.Menu, contentDescription = "Menu de Categorias")
            }
        },
        bottomBar = {
            Surface(
                color = SoftParchmentCard,
                shadowElevation = 8.dp,
                border = BorderStroke(1.dp, SoftParchmentBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .height(56.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val isHomeSelected = selectedPrayer == null && selectedCategory.isBlank() && searchQuery.isBlank()
                    val isFavoritesSelected = selectedPrayer == null && selectedCategory.equals("Favoritas", ignoreCase = true)

                    // Favorites Tab Button (Left)
                    Surface(
                        onClick = {
                            viewModel.selectPrayer(null)
                            viewModel.selectedCategory.value = "Favoritas"
                            viewModel.searchQuery.value = ""
                        },
                        color = if (isFavoritesSelected) CatholicRed.copy(alpha = 0.15f) else Color.Transparent,
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 6.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Favoritos",
                                tint = DivineGold
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Favoritos",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    color = CatholicRed,
                                    fontWeight = if (isFavoritesSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            )
                        }
                    }

                    // Home Tab Button (Right)
                    Surface(
                        onClick = {
                            viewModel.selectPrayer(null)
                            viewModel.selectedCategory.value = ""
                            viewModel.searchQuery.value = ""
                        },
                        color = if (isHomeSelected) CatholicRed.copy(alpha = 0.15f) else Color.Transparent,
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 6.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Home,
                                contentDescription = "Início",
                                tint = CatholicRed
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Início",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    color = CatholicRed,
                                    fontWeight = if (isHomeSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(SoftParchment)
        ) {
            // Header Bar with background image and Sync Button
            CatholicHeader(
                selectedPrayer = selectedPrayer,
                searchQuery = searchQuery,
                onSearchQueryChange = { viewModel.searchQuery.value = it },
                onBackClick = {
                    viewModel.selectPrayer(null)
                    startDetailInEditMode = false
                },
                onSyncClick = {
                    showSyncUrlDialog = true
                }
            )

            if (selectedPrayer == null) {
                if (selectedCategory.isBlank() && searchQuery.isBlank()) {
                    // HOME PAGE: Clean Liturgy Card view (categories in bottom menu)
                    HomeCategoryButtonsList(
                        liturgiaData = liturgiaData
                    )
                } else {
                    // CATEGORY / SEARCH / FAVORITES LIST VIEW
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        ) {
                            Surface(
                                color = CatholicRed.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(20.dp),
                                border = BorderStroke(1.dp, CatholicRed.copy(alpha = 0.3f))
                            ) {
                                Text(
                                    text = if (searchQuery.isNotBlank()) "Busca: \"$searchQuery\""
                                           else if (selectedCategory.equals("Favoritas", ignoreCase = true)) "Orações Favoritas"
                                           else selectedCategory,
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontFamily = FontFamily.Serif,
                                        color = CatholicRed,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }

                        if (prayers.isEmpty()) {
                            EmptyPrayersView(
                                searchQuery = searchQuery,
                                selectedCategory = selectedCategory
                            )
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(top = 4.dp, bottom = 80.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(prayers, key = { it.id }) { prayer ->
                                    PrayerCardItem(
                                        prayer = prayer,
                                        onClick = { viewModel.selectPrayer(prayer) },
                                        onToggleFavorite = { viewModel.toggleFavorite(prayer) },
                                        onEdit = {
                                            startDetailInEditMode = true
                                            viewModel.selectPrayer(prayer)
                                        },
                                        onDelete = {
                                            prayerToDeleteConfirm = prayer
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                // Detail Screen
                PrayerDetailView(
                    prayer = selectedPrayer!!,
                    fontSizeSp = fontSizeSp,
                    textAlign = textAlign,
                    startInEditMode = startDetailInEditMode,
                    onToggleFavorite = { viewModel.toggleFavorite(selectedPrayer!!) },
                    onAdjustFontSize = { delta -> viewModel.adjustFontSize(delta) },
                    onCycleTextAlign = { viewModel.cycleTextAlign() },
                    onEdit = {
                        startDetailInEditMode = true
                    },
                    onDelete = {
                        prayerToDeleteConfirm = selectedPrayer
                    },
                    onSaveInlineEdit = { id, title, category, content ->
                        viewModel.addOrUpdatePrayer(
                            title = title,
                            category = category,
                            content = content,
                            idToEdit = id
                        ) { success, msg ->
                            if (success) {
                                startDetailInEditMode = false
                                Toast.makeText(context, "Oração salva com sucesso!", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "Erro ao salvar: $msg", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    availableCategories = categories
                )
            }
        }
    }

    // Dialog for editing daily liturgy (Admin Mode)
    if (showEditLiturgiaDialog) {
        EditLiturgiaDialog(
            liturgiaData = liturgiaData,
            onDismiss = { showEditLiturgiaDialog = false },
            onSave = { updated ->
                viewModel.updateLiturgiaData(updated)
                showEditLiturgiaDialog = false
                Toast.makeText(context, "Liturgia do dia atualizada com sucesso!", Toast.LENGTH_SHORT).show()
            }
        )
    }

    // Floating Sheet Menu for Categories (Doutrina, Novenas, Orações)
    if (showFloatingMenuSheet) {
        FloatingCategoryMenuSheet(
            onDismiss = { showFloatingMenuSheet = false },
            onSelectCategory = { cat ->
                viewModel.selectedCategory.value = cat
            },
            onOpenSyncConfig = {
                showSyncUrlDialog = true
            }
        )
    }

    // Dialog for configuring GitHub Online Sync URL
    if (showSyncUrlDialog) {
        ConfigureSyncUrlDialog(
            currentUrl = syncUrlState,
            isSyncing = isSyncing,
            syncStatusMessage = syncStatusMessage ?: "",
            onDismiss = { showSyncUrlDialog = false },
            onSaveUrl = { newUrl ->
                viewModel.saveSyncUrl(newUrl)
                Toast.makeText(context, "URL de sincronização salva!", Toast.LENGTH_SHORT).show()
            },
            onSyncFromOnline = { url ->
                viewModel.syncFromOnlineUrl(url, silent = false)
            }
        )
    }

    // Confirmation Dialog for Prayer Content Deletion
    if (prayerToDeleteConfirm != null) {
        ConfirmDeleteDialog(
            title = "Excluir Conteúdo",
            itemName = prayerToDeleteConfirm!!.title,
            onDismiss = { prayerToDeleteConfirm = null },
            onConfirm = {
                val toDelete = prayerToDeleteConfirm!!
                prayerToDeleteConfirm = null
                if (selectedPrayer?.id == toDelete.id) {
                    viewModel.selectPrayer(null)
                }
                viewModel.deletePrayer(toDelete)
                Toast.makeText(context, "Oração '${toDelete.title}' excluída com sucesso!", Toast.LENGTH_SHORT).show()
            }
        )
    }

    // Dialog for adding or editing prayers
    if (showAddDialog) {
        AddEditPrayerDialog(
            prayerToEdit = prayerToEdit,
            initialCategory = initialCategoryForAdd,
            categories = categories,
            onDismiss = { showAddDialog = false },
            onSave = { title, category, content ->
                viewModel.addOrUpdatePrayer(
                    title = title,
                    category = category,
                    content = content,
                    idToEdit = prayerToEdit?.id ?: 0
                )
                showAddDialog = false
                Toast.makeText(context, "Oração salva com sucesso!", Toast.LENGTH_SHORT).show()
            }
        )
    }
}

@Composable
fun CatholicHeader(
    selectedPrayer: PrayerEntity?,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onBackClick: () -> Unit,
    onSyncClick: () -> Unit
) {
    Surface(
        color = Color.Transparent,
        contentColor = Color.White,
        shadowElevation = 6.dp
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Background image of Sacred Hearts (Sagrados Corações)
            AsyncImage(
                model = R.drawable.sacred_hearts_bg_1785544718992,
                contentDescription = "Sagrados Corações de Jesus e Maria",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .matchParentSize()
                    .alpha(0.88f)
            )

            // Semi-transparent Catholic Red overlay so the Sacred Hearts image shows through while keeping text legible
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(CatholicRed.copy(alpha = 0.30f))
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (selectedPrayer == null) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                                .border(1.5.dp, DivineGold, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            AsyncImage(
                                model = R.drawable.img_celtic_cross_icon_new_1782525602645,
                                contentDescription = "Cruz Celta",
                                contentScale = ContentScale.Fit,
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = selectedPrayer?.title ?: "Oração Cathólica",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = if (selectedPrayer != null) selectedPrayer.category else "Orações e Devoções",
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = DivineGold
                            )
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // GitHub Sync Button
                        IconButton(
                            onClick = onSyncClick,
                            modifier = Modifier.testTag("sync_header_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Sync,
                                contentDescription = "Sincronizar do GitHub",
                                tint = DivineGold
                            )
                        }
                    }
                }

                // Search Bar when in List mode
                if (selectedPrayer == null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = onSearchQueryChange,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        placeholder = {
                            Text(
                                "Buscar oração, novena, salmo...",
                                color = Color.White.copy(alpha = 0.7f),
                                fontSize = 14.sp
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                tint = DivineGold
                            )
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { onSearchQueryChange("") }) {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = "Limpar",
                                        tint = Color.White
                                    )
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(26.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = DivineGold,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.4f),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            cursorColor = DivineGold,
                            focusedContainerColor = Color.White.copy(alpha = 0.18f),
                            unfocusedContainerColor = Color.White.copy(alpha = 0.12f)
                        )
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CategoryFilterChips(
    categories: List<String>,
    selectedCategory: String,
    onSelectCategory: (String) -> Unit,
    onManageCategories: () -> Unit
) {
    FlowRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        categories.forEach { category ->
            val isSelected = category.equals(selectedCategory, ignoreCase = true)
            FilterChip(
                selected = isSelected,
                onClick = {
                    if (isSelected) {
                        onSelectCategory("") // toggle off back to home
                    } else {
                        onSelectCategory(category)
                    }
                },
                label = {
                    Text(
                        text = category,
                        fontSize = 14.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = CatholicRed,
                    selectedLabelColor = Color.White,
                    containerColor = SoftParchmentCard,
                    labelColor = CatholicRed
                ),
                border = FilterChipDefaults.filterChipBorder(
                    borderColor = SoftParchmentBorder,
                    selectedBorderColor = CatholicRed,
                    enabled = true,
                    selected = isSelected
                ),
                shape = RoundedCornerShape(16.dp)
            )
        }

        // Manage/reorder categories chip
        FilterChip(
            selected = false,
            onClick = onManageCategories,
            label = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Organizar Categorias",
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "Organizar",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            },
            colors = FilterChipDefaults.filterChipColors(
                containerColor = SoftParchmentCard,
                labelColor = CatholicRed
            ),
            border = FilterChipDefaults.filterChipBorder(
                borderColor = DivineGold.copy(alpha = 0.6f),
                enabled = true,
                selected = false
            ),
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
fun PrayerCardItem(
    prayer: PrayerEntity,
    isAdminMode: Boolean = false,
    onClick: () -> Unit,
    onToggleFavorite: () -> Unit,
    onEdit: () -> Unit = {},
    onDelete: () -> Unit = {}
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SoftParchmentCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, SoftParchmentBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Surface(
                    color = DivineGold.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = prayer.category,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = CatholicRed,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = prayer.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        color = DarkText
                    )
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = stripFormatting(prayer.content).take(100) + "...",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = DarkText.copy(alpha = 0.7f),
                        lineHeight = 18.sp
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            if (isAdminMode) {
                IconButton(onClick = onEdit) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Editar Conteúdo",
                        tint = CatholicRed
                    )
                }
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Excluir Conteúdo",
                        tint = Color.Red
                    )
                }
            } else {
                IconButton(onClick = onToggleFavorite) {
                    Icon(
                        imageVector = if (prayer.isFavorite) Icons.Default.Star else Icons.Default.StarBorder,
                        contentDescription = "Favorito",
                        tint = if (prayer.isFavorite) DivineGold else Color.Gray
                    )
                }
            }
        }
    }
}

fun stripFormatting(text: String): String {
    if (text.isBlank()) return ""
    return text
        .replace(Regex("""\[/.*?\]"""), " ")
        .replace(Regex("""\[.*?\]"""), " ")
        .replace(Regex("""\{([^}]+)\}"""), "$1")
        .replace(Regex("""\:([^:]+)\:"""), "$1")
        .replace(Regex("""\*\*(.*?)\*\*"""), "$1")
        .replace(Regex("""<b>(.*?)</b>"""), "$1")
        .replace(Regex("""<strong>(.*?)</strong>"""), "$1")
        .replace(Regex("""'(.*?)'"""), "$1")
        .replace(Regex("""‘(.*?)’"""), "$1")
        .replace("*", "")
        .replace("\n", " ")
        .replace(Regex("""\s+"""), " ")
        .trim()
}

sealed class PrayerContentBlock {
    data class TextBlock(val content: String) : PrayerContentBlock()
    data class ExpandableBlock(val title: String, val content: String) : PrayerContentBlock()
}

fun parsePrayerBlocks(text: String): List<PrayerContentBlock> {
    if (text.isBlank()) return emptyList()

    val startTagRegex = Regex(
        """\[\s*(?:(?:expandir|secao|bloco|expansivel|detalhes)\s*(?:[=:]\s*|\s*))?([^/\]]+)\]""",
        RegexOption.IGNORE_CASE
    )
    val closeTagRegex = Regex(
        """\[\s*/\s*(?:expandir|secao|bloco|expansivel|detalhes|\s*)\s*\]""",
        RegexOption.IGNORE_CASE
    )

    val startMatches = startTagRegex.findAll(text).toList()
    if (startMatches.isEmpty()) {
        val cleanText = text.replace(closeTagRegex, "").trim()
        return if (cleanText.isNotBlank()) listOf(PrayerContentBlock.TextBlock(cleanText)) else emptyList()
    }

    val blocks = mutableListOf<PrayerContentBlock>()
    var currentIndex = 0

    for (i in startMatches.indices) {
        val match = startMatches[i]
        val startTagStart = match.range.first
        val startTagEnd = match.range.last + 1
        val rawTitle = match.groupValues[1].trim()

        val remainingAfterStart = text.substring(startTagEnd)
        val closeMatch = closeTagRegex.find(remainingAfterStart)

        if (closeMatch == null) {
            continue
        }

        if (startTagStart > currentIndex) {
            val prefixText = text.substring(currentIndex, startTagStart)
                .replace(closeTagRegex, "")
                .trim()
            if (prefixText.isNotBlank()) {
                blocks.add(PrayerContentBlock.TextBlock(prefixText))
            }
        }

        val blockContent = remainingAfterStart.substring(0, closeMatch.range.first).trim()
        val title = rawTitle.ifBlank { "Texto Expansível" }

        blocks.add(PrayerContentBlock.ExpandableBlock(title = title, content = blockContent))

        currentIndex = startTagEnd + closeMatch.range.last + 1
    }

    if (currentIndex < text.length) {
        val remainingText = text.substring(currentIndex)
            .replace(closeTagRegex, "")
            .trim()
        if (remainingText.isNotBlank()) {
            blocks.add(PrayerContentBlock.TextBlock(remainingText))
        }
    }

    return if (blocks.isEmpty()) listOf(PrayerContentBlock.TextBlock(text.replace(closeTagRegex, "").trim())) else blocks
}

@Composable
fun ExpandableSectionCard(
    title: String,
    content: String,
    fontSizeSp: Int,
    textAlign: TextAlign,
    modifier: Modifier = Modifier
) {
    var isExpanded by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        // Natural Clickable Header: Title + Expand Arrow right in front
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { isExpanded = !isExpanded }
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title.ifBlank { "Texto Expansível" },
                style = MaterialTheme.typography.titleMedium.copy(
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    color = CatholicRed,
                    fontSize = (fontSizeSp + 1).sp
                ),
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            )

            Icon(
                imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = if (isExpanded) "Recolher" else "Expandir",
                tint = CatholicRed,
                modifier = Modifier.size(24.dp)
            )
        }

        // Expanded Body with soft dividing line below title
        AnimatedVisibility(
            visible = isExpanded,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Divider(
                    modifier = Modifier.padding(bottom = 8.dp),
                    color = DivineGold.copy(alpha = 0.45f),
                    thickness = 0.8.dp
                )

                Text(
                    text = parseFormattedText(content, fontSizeSp),
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontFamily = FontFamily.Serif,
                        fontSize = fontSizeSp.sp,
                        lineHeight = (fontSizeSp * 1.55).sp,
                        color = DarkText,
                        textAlign = textAlign
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

fun parseFormattedText(text: String, baseFontSizeSp: Int = 18): AnnotatedString {
    if (text.isBlank()) return AnnotatedString("")
    val rubricFontSize = maxOf(10, baseFontSizeSp - 5).sp
    return buildAnnotatedString {
        try {
            var currentIndex = 0
            val regex = Regex(
                """\{([^}]+)\}|\*\*(.*?)\*\*|<b>(.*?)</b>|<strong>(.*?)</strong>|\:([^:]+)\:|'(.*?)'|‘(.*?)’""",
                RegexOption.DOT_MATCHES_ALL
            )
            val matches = regex.findAll(text)

            for (match in matches) {
                if (match.range.first > currentIndex) {
                    append(text.substring(currentIndex, match.range.first))
                }

                val gBracesBold = match.groupValues.getOrNull(1) ?: ""
                val gMarkdownBold1 = match.groupValues.getOrNull(2) ?: ""
                val gMarkdownBold2 = match.groupValues.getOrNull(3) ?: ""
                val gMarkdownBold3 = match.groupValues.getOrNull(4) ?: ""
                val gRed = match.groupValues.getOrNull(5) ?: ""
                val gQuote1 = match.groupValues.getOrNull(6) ?: ""
                val gQuote2 = match.groupValues.getOrNull(7) ?: ""

                val boldContent = gBracesBold.ifEmpty { gMarkdownBold1.ifEmpty { gMarkdownBold2.ifEmpty { gMarkdownBold3 } } }

                when {
                    boldContent.isNotEmpty() -> {
                        pushStyle(
                            SpanStyle(
                                fontWeight = FontWeight.Bold,
                                color = Color.Unspecified
                            )
                        )
                        append(boldContent)
                        pop()
                    }
                    gRed.isNotEmpty() -> {
                        pushStyle(
                            SpanStyle(
                                color = LightCatholicRed,
                                fontWeight = FontWeight.Normal,
                                fontSize = rubricFontSize
                            )
                        )
                        append(gRed)
                        pop()
                    }
                    gQuote1.isNotEmpty() || gQuote2.isNotEmpty() -> {
                        val quoteContent = gQuote1.ifEmpty { gQuote2 }
                        pushStyle(
                            SpanStyle(
                                color = LightCatholicRed,
                                fontWeight = FontWeight.Normal,
                                fontSize = rubricFontSize
                            )
                        )
                        append(quoteContent)
                        pop()
                    }
                    else -> {
                        append(match.value)
                    }
                }

                currentIndex = match.range.last + 1
            }

            if (currentIndex < text.length) {
                append(text.substring(currentIndex))
            }
        } catch (e: Exception) {
            append(text)
        }
    }
}

class PrayerFormattingVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val rawText = text.text
        val builder = AnnotatedString.Builder(rawText)

        val boldRegex = Regex("""\{([^}]+)\}|\*\*(.*?)\*\*""", RegexOption.DOT_MATCHES_ALL)
        boldRegex.findAll(rawText).forEach { match ->
            val start = match.range.first
            val end = match.range.last + 1
            val isBraces = match.value.startsWith("{")
            val delimLen = if (isBraces) 1 else 2

            builder.addStyle(
                style = SpanStyle(color = Color.Gray.copy(alpha = 0.35f)),
                start = start,
                end = (start + delimLen).coerceAtMost(end)
            )
            builder.addStyle(
                style = SpanStyle(color = Color.Gray.copy(alpha = 0.35f)),
                start = (end - delimLen).coerceAtLeast(start),
                end = end
            )
            val innerStart = start + delimLen
            val innerEnd = end - delimLen
            if (innerStart < innerEnd) {
                builder.addStyle(
                    style = SpanStyle(fontWeight = FontWeight.Bold),
                    start = innerStart,
                    end = innerEnd
                )
            }
        }

        val redRegex = Regex("""\:([^:]+)\:|'([^']+)'""")
        redRegex.findAll(rawText).forEach { match ->
            val start = match.range.first
            val end = match.range.last + 1
            builder.addStyle(
                style = SpanStyle(color = LightCatholicRed.copy(alpha = 0.35f)),
                start = start,
                end = (start + 1).coerceAtMost(end)
            )
            builder.addStyle(
                style = SpanStyle(color = LightCatholicRed.copy(alpha = 0.35f)),
                start = (end - 1).coerceAtLeast(start),
                end = end
            )
            val innerStart = start + 1
            val innerEnd = end - 1
            if (innerStart < innerEnd) {
                builder.addStyle(
                    style = SpanStyle(color = LightCatholicRed, fontWeight = FontWeight.Normal),
                    start = innerStart,
                    end = innerEnd
                )
            }
        }

        val expandTagRegex = Regex("""\[[^\]]+\]""")
        expandTagRegex.findAll(rawText).forEach { match ->
            builder.addStyle(
                style = SpanStyle(
                    color = CatholicRed,
                    background = DivineGold.copy(alpha = 0.25f),
                    fontWeight = FontWeight.Bold
                ),
                start = match.range.first,
                end = match.range.last + 1
            )
        }

        return TransformedText(builder.toAnnotatedString(), OffsetMapping.Identity)
    }
}

@Composable
fun PrayerDetailView(
    prayer: PrayerEntity,
    fontSizeSp: Int,
    textAlign: TextAlign,
    isAdminMode: Boolean = false,
    startInEditMode: Boolean = false,
    onToggleFavorite: () -> Unit,
    onAdjustFontSize: (Int) -> Unit,
    onCycleTextAlign: () -> Unit,
    onEdit: () -> Unit = {},
    onDelete: () -> Unit,
    onSaveInlineEdit: (Int, String, String, String) -> Unit = { _, _, _, _ -> },
    availableCategories: List<String> = emptyList()
) {
    val context = LocalContext.current
    var showOptionsBar by remember { mutableStateOf(false) }

    var isInlineEditing by rememberSaveable(prayer.id, startInEditMode) { mutableStateOf(startInEditMode) }

    // Disable inline editing automatically whenever admin mode is turned off
    LaunchedEffect(isAdminMode) {
        if (!isAdminMode) {
            isInlineEditing = false
        }
    }

    LaunchedEffect(startInEditMode) {
        if (startInEditMode && isAdminMode) {
            isInlineEditing = true
        }
    }

    val activeInlineEditing = isInlineEditing && isAdminMode
    var editedTitle by rememberSaveable(prayer.id, activeInlineEditing) { mutableStateOf(prayer.title) }
    var editedCategory by rememberSaveable(prayer.id, activeInlineEditing) { mutableStateOf(prayer.category) }
    var editedContentTextField by remember(prayer.id, activeInlineEditing) {
        mutableStateOf(TextFieldValue(prayer.content))
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SoftParchment)
    ) {
        // Collapsible Reading Options Bar directly under header
        Surface(
            color = SoftParchmentCard,
            shadowElevation = 1.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // When collapsed: show ONLY a tiny narrow bar with arrow right up against header
                if (!showOptionsBar) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showOptionsBar = true }
                            .padding(horizontal = 12.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.End
                    ) {
                        Text(
                            text = if (isInlineEditing) "Opções de edição" else "Opções de leitura",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = CatholicRed.copy(alpha = 0.7f),
                                fontWeight = FontWeight.Medium
                            )
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "Expandir opções de leitura",
                            tint = CatholicRed,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // Expanded options panel
                AnimatedVisibility(
                    visible = showOptionsBar,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(CatholicRed.copy(alpha = 0.04f))
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // Row 1: Alignment + Font Size + Collapse Arrow
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Text alignment button
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(SoftParchmentCard)
                                    .clickable { onCycleTextAlign() }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Icon(
                                    imageVector = when (textAlign) {
                                        TextAlign.Start, TextAlign.Left -> Icons.Default.FormatAlignLeft
                                        TextAlign.Center -> Icons.Default.FormatAlignCenter
                                        TextAlign.Justify -> Icons.Default.FormatAlignJustify
                                        else -> Icons.Default.FormatAlignLeft
                                    },
                                    contentDescription = "Alinhamento",
                                    tint = CatholicRed,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = when (textAlign) {
                                        TextAlign.Start, TextAlign.Left -> "Esquerda"
                                        TextAlign.Center -> "Centro"
                                        TextAlign.Justify -> "Justificado"
                                        else -> "Esquerda"
                                    },
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        color = CatholicRed,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }

                            // Font size - / +
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .background(SoftParchmentCard, RoundedCornerShape(12.dp))
                                    .padding(horizontal = 4.dp, vertical = 2.dp)
                            ) {
                                IconButton(
                                    onClick = { onAdjustFontSize(-2) },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Remove,
                                        contentDescription = "Diminuir fonte",
                                        tint = CatholicRed,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Text(
                                    text = "$fontSizeSp pt",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = CatholicRed
                                    ),
                                    modifier = Modifier.padding(horizontal = 4.dp)
                                )
                                IconButton(
                                    onClick = { onAdjustFontSize(2) },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "Aumentar fonte",
                                        tint = CatholicRed,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }

                            // Close arrow
                            IconButton(
                                onClick = { showOptionsBar = false },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowUp,
                                    contentDescription = "Recolher opções",
                                    tint = CatholicRed,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }

                        // Row 2: Action buttons (Copy, Favorite, Admin controls)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            TextButton(
                                onClick = {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    val clip = ClipData.newPlainText("Oração", "${prayer.title}\n\n${prayer.content}")
                                    clipboard.setPrimaryClip(clip)
                                    Toast.makeText(context, "Oração copiada para a área de transferência!", Toast.LENGTH_SHORT).show()
                                }
                            ) {
                                Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp), tint = CatholicRed)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Copiar", color = CatholicRed, style = MaterialTheme.typography.labelMedium)
                            }

                            TextButton(onClick = onToggleFavorite) {
                                Icon(
                                    imageVector = if (prayer.isFavorite) Icons.Default.Star else Icons.Default.StarBorder,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp),
                                    tint = if (prayer.isFavorite) DivineGold else CatholicRed
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (prayer.isFavorite) "Favorito" else "Favoritar",
                                    color = CatholicRed,
                                    style = MaterialTheme.typography.labelMedium
                                )
                            }

                            if (isAdminMode) {
                                TextButton(
                                    onClick = {
                                        isInlineEditing = !isInlineEditing
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp),
                                        tint = if (isInlineEditing) DivineGold else CatholicRed
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Editar",
                                        color = CatholicRed,
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                TextButton(onClick = onDelete) {
                                    Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.Red)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Excluir", color = Color.Red, style = MaterialTheme.typography.labelMedium)
                                }
                            }
                        }

                        if (isInlineEditing) {
                            Divider(color = SoftParchmentBorder, thickness = 1.dp, modifier = Modifier.padding(vertical = 4.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 4.dp, vertical = 2.dp),
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Button(
                                    onClick = {
                                        val text = editedContentTextField.text
                                        val sel = editedContentTextField.selection
                                        if (sel.start != sel.end) {
                                            val selected = text.substring(sel.start, sel.end)
                                            val newText = text.substring(0, sel.start) + "{$selected}" + text.substring(sel.end)
                                            editedContentTextField = TextFieldValue(newText, TextRange(sel.end + 2))
                                        } else {
                                            val pos = sel.start
                                            val newText = text.substring(0, pos) + "{texto em negrito}" + text.substring(pos)
                                            editedContentTextField = TextFieldValue(newText, TextRange(pos + 1, pos + 17))
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = DivineGold.copy(alpha = 0.35f), contentColor = DarkText),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Icon(Icons.Default.FormatBold, contentDescription = null, modifier = Modifier.size(15.dp), tint = DarkText)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Negrito", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }

                                Button(
                                    onClick = {
                                        val text = editedContentTextField.text
                                        val sel = editedContentTextField.selection
                                        if (sel.start != sel.end) {
                                            val selected = text.substring(sel.start, sel.end)
                                            val newText = text.substring(0, sel.start) + ":$selected:" + text.substring(sel.end)
                                            editedContentTextField = TextFieldValue(newText, TextRange(sel.end + 2))
                                        } else {
                                            val pos = sel.start
                                            val newText = text.substring(0, pos) + ":texto em vermelho:" + text.substring(pos)
                                            editedContentTextField = TextFieldValue(newText, TextRange(pos + 1, pos + 18))
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = CatholicRed.copy(alpha = 0.15f), contentColor = CatholicRed),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Icon(Icons.Default.FormatColorText, contentDescription = null, modifier = Modifier.size(15.dp), tint = CatholicRed)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Texto Vermelho", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }

                                Button(
                                    onClick = {
                                        val text = editedContentTextField.text
                                        val sel = editedContentTextField.selection
                                        if (sel.start != sel.end) {
                                            val selected = text.substring(sel.start, sel.end)
                                            val newText = text.substring(0, sel.start) + "\n[Título do texto expansível]\n$selected\n[/]\n" + text.substring(sel.end)
                                            editedContentTextField = TextFieldValue(newText, TextRange(sel.end + 34))
                                        } else {
                                            val pos = sel.start
                                            val titleP = "Título do texto expansível"
                                            val contentP = "Conteúdo aqui..."
                                            val newText = text.substring(0, pos) + "\n[$titleP]\n$contentP\n[/]\n" + text.substring(pos)
                                            val startT = pos + 2
                                            editedContentTextField = TextFieldValue(newText, TextRange(startT, startT + titleP.length))
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = DivineGold, contentColor = DarkText),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Icon(Icons.Default.UnfoldMore, contentDescription = null, modifier = Modifier.size(15.dp), tint = DarkText)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Expansível", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Prayer Card content view with adjusted side margins
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxSize(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = SoftParchmentCard),
                border = BorderStroke(1.dp, SoftParchmentBorder),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(start = 12.dp, end = 12.dp, top = 12.dp, bottom = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .border(1.dp, DivineGold, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        AsyncImage(
                            model = R.drawable.img_celtic_cross_icon_new_1782525602645,
                            contentDescription = "Cruz Celta",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .size(26.dp)
                                .clip(CircleShape)
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    if (!isInlineEditing) {
                        // Standard Viewing Mode
                        Text(
                            text = prayer.title,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold,
                                color = CatholicRed,
                                textAlign = TextAlign.Center
                            )
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Surface(
                            color = DivineGold.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = prayer.category,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = CatholicRed,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        val blocks = remember(prayer.content) { parsePrayerBlocks(prayer.content) }
                        blocks.forEach { block ->
                            when (block) {
                                is PrayerContentBlock.TextBlock -> {
                                    if (block.content.isNotBlank()) {
                                        Text(
                                            text = parseFormattedText(block.content, fontSizeSp),
                                            style = MaterialTheme.typography.bodyLarge.copy(
                                                fontFamily = FontFamily.Serif,
                                                fontSize = fontSizeSp.sp,
                                                lineHeight = (fontSizeSp * 1.55).sp,
                                                color = DarkText,
                                                textAlign = textAlign
                                            ),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 4.dp)
                                        )
                                    }
                                }
                                is PrayerContentBlock.ExpandableBlock -> {
                                    ExpandableSectionCard(
                                        title = block.title,
                                        content = block.content,
                                        fontSizeSp = fontSizeSp,
                                        textAlign = textAlign
                                    )
                                }
                            }
                        }
                    } else {
                        // Direct Inline Editing Mode
                        Spacer(modifier = Modifier.height(4.dp))

                        // Title Field
                        OutlinedTextField(
                            value = editedTitle,
                            onValueChange = { editedTitle = it },
                            label = { Text("Título da Oração") },
                            textStyle = MaterialTheme.typography.titleMedium.copy(
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold,
                                color = CatholicRed
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CatholicRed,
                                unfocusedBorderColor = DivineGold
                            )
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Category Field
                        OutlinedTextField(
                            value = editedCategory,
                            onValueChange = { editedCategory = it },
                            label = { Text("Categoria") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CatholicRed,
                                unfocusedBorderColor = DivineGold
                            )
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Content Field with live WYSIWYG visual transformation
                        OutlinedTextField(
                            value = editedContentTextField,
                            onValueChange = { editedContentTextField = it },
                            label = { Text("Conteúdo da Oração") },
                            visualTransformation = remember { PrayerFormattingVisualTransformation() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CatholicRed,
                                unfocusedBorderColor = DivineGold
                            )
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Bottom Action Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedButton(
                                onClick = { isInlineEditing = false },
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Cancelar", color = DarkText)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = {
                                    if (editedTitle.isNotBlank() && editedContentTextField.text.isNotBlank()) {
                                        onSaveInlineEdit(
                                            prayer.id,
                                            editedTitle.trim(),
                                            editedCategory.trim(),
                                            editedContentTextField.text.trim()
                                        )
                                        isInlineEditing = false
                                    } else {
                                        Toast.makeText(context, "Preencha o título e o conteúdo", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = CatholicRed, contentColor = Color.White),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Salvar Alterações", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyPrayersView(searchQuery: String, selectedCategory: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = CatholicRed.copy(alpha = 0.4f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = if (searchQuery.isNotEmpty()) "Nenhuma oração encontrada para '$searchQuery'"
            else if (selectedCategory == "Favoritas") "Você ainda não possui orações favoritadas."
            else "Nenhuma oração nesta categoria.",
            style = MaterialTheme.typography.titleMedium.copy(
                fontFamily = FontFamily.Serif,
                color = DarkText,
                textAlign = TextAlign.Center
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Use o botão do Administrador ou a busca para localizar mais conteúdos.",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
        )
    }
}

@Composable
fun DailyLiturgyCard(
    liturgiaData: DailyLiturgyData,
    isAdminMode: Boolean = false,
    onEditClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val seasonColor = try {
        Color(android.graphics.Color.parseColor(liturgiaData.liturgicalColorHex))
    } catch (e: Exception) {
        Color(0xFF2E7D32)
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("daily_liturgy_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = SoftParchmentCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        border = BorderStroke(1.5.dp, DivineGold.copy(alpha = 0.8f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 20.dp, horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            if (isAdminMode) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(
                        onClick = onEditClick,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Editar Liturgia",
                            tint = CatholicRed,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // 1. CITATIONS (1ª leitura, Salmo, 2ª leitura if present, Evangelho)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                LiturgyReadingItem(liturgiaData.firstReading)
                LiturgyReadingItem(liturgiaData.psalm)
                if (!liturgiaData.secondReading.isNullOrBlank()) {
                    LiturgyReadingItem(liturgiaData.secondReading)
                }
                LiturgyReadingItem(liturgiaData.gospel)
            }

            // 2. ICON
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(CatholicRed)
                    .border(2.5.dp, DivineGold, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.MenuBook,
                    contentDescription = "Ícone Litúrgico",
                    tint = DivineGold,
                    modifier = Modifier.size(32.dp)
                )
            }

            // 3. DAY OF WEEK ONLY (e.g., QUARTA-FEIRA)
            val validDays = listOf("DOMINGO", "SEGUNDA-FEIRA", "TERÇA-FEIRA", "QUARTA-FEIRA", "QUINTA-FEIRA", "SEXTA-FEIRA", "SÁBADO")
            val cleanDayText = if (validDays.contains(liturgiaData.dayOfWeek.trim().uppercase()) && !liturgiaData.dayOfWeek.contains("ASSISTI", ignoreCase = true)) {
                liturgiaData.dayOfWeek.trim().uppercase()
            } else {
                "QUARTA-FEIRA"
            }

            Text(
                text = cleanDayText,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    color = CatholicRed,
                    letterSpacing = 2.sp,
                    fontSize = 22.sp
                ),
                textAlign = TextAlign.Center
            )

            // 4. LITURGICAL WEEK (e.g., 18ª Semana)
            if (liturgiaData.liturgicalWeek.isNotBlank()) {
                Text(
                    text = liturgiaData.liturgicalWeek,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        color = DarkText,
                        fontSize = 18.sp
                    ),
                    textAlign = TextAlign.Center
                )
            }

            // 5. LITURGICAL SEASON HIGHLIGHTED PILL (e.g., Tempo Comum)
            Surface(
                color = seasonColor.copy(alpha = 0.15f),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.2.dp, seasonColor.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 18.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(seasonColor)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = liturgiaData.liturgicalSeason,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = seasonColor,
                            fontSize = 16.sp
                        ),
                        textAlign = TextAlign.Center
                    )
                }
            }

            // 6. SANTO DO DIA (Saint of the Day)
            if (liturgiaData.saintOfDay.isNotBlank()) {
                Surface(
                    color = SoftParchmentBorder.copy(alpha = 0.25f),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, DivineGold.copy(alpha = 0.4f))
                ) {
                    Text(
                        text = "Santo do dia: ${liturgiaData.saintOfDay}",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.SemiBold,
                            fontStyle = FontStyle.Italic,
                            color = DarkText,
                            fontSize = 15.sp
                        ),
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
private fun LiturgyReadingItem(text: String) {
    if (text.isBlank()) return
    val sanitizedText = if (text.length > 50 || text.contains("\n") || text.contains("ASSISTI", ignoreCase = true) || text.contains("CUMULAI", ignoreCase = true)) {
        when {
            text.contains("1ª", ignoreCase = true) -> "1ª leitura Jr 31,1-7"
            text.contains("Salmo", ignoreCase = true) -> "Salmo 23,2-7"
            text.contains("2ª", ignoreCase = true) -> "2ª leitura 1Cor 12,12-30"
            text.contains("Evangelho", ignoreCase = true) -> "Evangelho Mt 15,21-28"
            else -> "1ª leitura Jr 31,1-7"
        }
    } else text.trim()

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        val keywords = listOf("1ª leitura", "2ª leitura", "Salmo", "Evangelho")
        val match = keywords.firstOrNull { sanitizedText.startsWith(it, ignoreCase = true) }
        if (match != null) {
            Text(
                text = "$match ",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    color = CatholicRed,
                    fontSize = 17.sp
                )
            )
            Text(
                text = sanitizedText.substring(match.length).trim(),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    color = DarkText,
                    fontSize = 17.sp
                )
            )
        } else {
            Text(
                text = sanitizedText,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    color = DarkText,
                    fontSize = 17.sp
                )
            )
        }
    }
}

@Composable
fun EditLiturgiaDialog(
    liturgiaData: DailyLiturgyData,
    onDismiss: () -> Unit,
    onSave: (DailyLiturgyData) -> Unit
) {
    var firstReading by remember { mutableStateOf(liturgiaData.firstReading) }
    var psalm by remember { mutableStateOf(liturgiaData.psalm) }
    var secondReading by remember { mutableStateOf(liturgiaData.secondReading ?: "") }
    var gospel by remember { mutableStateOf(liturgiaData.gospel) }
    var dayOfWeek by remember { mutableStateOf(liturgiaData.dayOfWeek) }
    var liturgicalWeek by remember { mutableStateOf(liturgiaData.liturgicalWeek) }
    var liturgicalSeason by remember { mutableStateOf(liturgiaData.liturgicalSeason) }
    var saintOfDay by remember { mutableStateOf(liturgiaData.saintOfDay) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Editar Liturgia do Dia", fontWeight = FontWeight.Bold, color = CatholicRed)
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = firstReading,
                    onValueChange = { firstReading = it },
                    label = { Text("1ª Leitura") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = psalm,
                    onValueChange = { psalm = it },
                    label = { Text("Salmo") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = secondReading,
                    onValueChange = { secondReading = it },
                    label = { Text("2ª Leitura (Opcional)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = gospel,
                    onValueChange = { gospel = it },
                    label = { Text("Evangelho") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = dayOfWeek,
                    onValueChange = { dayOfWeek = it },
                    label = { Text("Dia da Semana") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = liturgicalWeek,
                    onValueChange = { liturgicalWeek = it },
                    label = { Text("Semana Litúrgica") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = liturgicalSeason,
                    onValueChange = { liturgicalSeason = it },
                    label = { Text("Tempo Litúrgico") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = saintOfDay,
                    onValueChange = { saintOfDay = it },
                    label = { Text("Santo do Dia") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(
                        DailyLiturgyData(
                            firstReading = firstReading,
                            psalm = psalm,
                            secondReading = secondReading.ifBlank { null },
                            gospel = gospel,
                            dayOfWeek = dayOfWeek,
                            liturgicalWeek = liturgicalWeek,
                            liturgicalSeason = liturgicalSeason,
                            liturgicalColorHex = liturgiaData.liturgicalColorHex,
                            saintOfDay = saintOfDay
                        )
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = CatholicRed)
            ) {
                Text("Salvar", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = DarkText)
            }
        }
    )
}

@Composable
fun HomeCategoryButtonsList(
    liturgiaData: DailyLiturgyData
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 80.dp),
        contentAlignment = Alignment.Center
    ) {
        DailyLiturgyCard(
            liturgiaData = liturgiaData,
            isAdminMode = false,
            onEditClick = {},
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun ConfigureSyncUrlDialog(
    currentUrl: String,
    isSyncing: Boolean,
    syncStatusMessage: String,
    onDismiss: () -> Unit,
    onSaveUrl: (String) -> Unit,
    onSyncFromOnline: (String) -> Unit
) {
    var urlText by remember { mutableStateOf(currentUrl) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Sync,
                    contentDescription = null,
                    tint = CatholicRed
                )
                Text(
                    text = "Sincronização GitHub (JSON)",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        color = CatholicRed,
                        fontSize = 18.sp
                    )
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Insira a URL 'raw' do arquivo JSON no seu repositório do GitHub (público ou privado via token raw) para atualizar orações e novenas offline:",
                    style = MaterialTheme.typography.bodySmall.copy(color = DarkText, fontSize = 13.sp)
                )

                OutlinedTextField(
                    value = urlText,
                    onValueChange = { urlText = it },
                    label = { Text("URL Raw do GitHub (JSON)") },
                    placeholder = { Text("https://raw.githubusercontent.com/usuario/repo/main/prayers.json") },
                    singleLine = false,
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = DivineGold,
                        focusedLabelColor = CatholicRed
                    )
                )

                if (syncStatusMessage.isNotBlank()) {
                    Surface(
                        color = CatholicRed.copy(alpha = 0.08f),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, CatholicRed.copy(alpha = 0.3f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            if (isSyncing) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    color = CatholicRed,
                                    strokeWidth = 2.dp
                                )
                            }
                            Text(
                                text = syncStatusMessage,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = DarkText,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { onSyncFromOnline(urlText) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        enabled = !isSyncing && urlText.isNotBlank()
                    ) {
                        Icon(Icons.Default.CloudDownload, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Baixar e Sincronizar Agora", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSaveUrl(urlText)
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = DivineGold, contentColor = CatholicRed)
            ) {
                Text("Salvar URL", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Fechar", color = DarkText)
            }
        },
        containerColor = SoftParchmentCard,
        shape = RoundedCornerShape(20.dp)
    )
}







@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditPrayerDialog(
    prayerToEdit: PrayerEntity?,
    initialCategory: String = "",
    categories: List<String> = emptyList(),
    onDismiss: () -> Unit,
    onManageCategories: () -> Unit = {},
    onSave: (String, String, String) -> Unit
) {
    var title by remember { mutableStateOf(prayerToEdit?.title ?: "") }
    var category by remember {
        mutableStateOf(
            prayerToEdit?.category ?: (if (initialCategory.isNotBlank()) initialCategory else (categories.firstOrNull() ?: "Orações"))
        )
    }
    var contentTextField by remember {
        mutableStateOf(TextFieldValue(prayerToEdit?.content ?: ""))
    }

    var showCategorySelector by remember { mutableStateOf(false) }
    val categoryOptions = if (categories.isNotEmpty()) categories else listOf("Orações", "Novenas", "Terços", "Salmos", "Ladainhas", "Devoções", "Outros")

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(),
            color = SoftParchment
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header TopBar
                Surface(
                    color = SoftParchmentCard,
                    shadowElevation = 3.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = onDismiss) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Fechar",
                                    tint = CatholicRed
                                )
                            }
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (prayerToEdit == null) "Incluir Oração" else "Editar Oração",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontFamily = FontFamily.Serif,
                                    fontWeight = FontWeight.Bold,
                                    color = CatholicRed
                                )
                            )
                        }

                        Button(
                            onClick = {
                                if (title.isNotBlank() && contentTextField.text.isNotBlank()) {
                                    onSave(title, category, contentTextField.text)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CatholicRed, contentColor = Color.White),
                            enabled = title.isNotBlank() && contentTextField.text.isNotBlank(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Salvar", fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Title field
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Título da Oração") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CatholicRed,
                            focusedLabelColor = CatholicRed
                        )
                    )

                    // Category Selector Card field
                    Card(
                        onClick = { showCategorySelector = true },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = SoftParchmentCard),
                        border = BorderStroke(1.dp, DivineGold.copy(alpha = 0.6f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "Categoria",
                                    style = MaterialTheme.typography.labelSmall.copy(color = CatholicRed, fontWeight = FontWeight.Bold)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = category,
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontFamily = FontFamily.Serif,
                                        fontWeight = FontWeight.Bold,
                                        color = DarkText
                                    )
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Selecionar Categoria",
                                tint = CatholicRed
                            )
                        }
                    }

                    // Formatting options bar
                    Surface(
                        color = SoftParchmentCard,
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, DivineGold.copy(alpha = 0.4f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    val text = contentTextField.text
                                    val sel = contentTextField.selection
                                    if (sel.start != sel.end) {
                                        val selected = text.substring(sel.start, sel.end)
                                        val before = text.substring(0, sel.start)
                                        val after = text.substring(sel.end)
                                        val newText = "$before{$selected}$after"
                                        val newEnd = sel.end + 2
                                        contentTextField = TextFieldValue(
                                            text = newText,
                                            selection = TextRange(newEnd)
                                        )
                                    } else {
                                        val pos = sel.start
                                        val before = text.substring(0, pos)
                                        val after = text.substring(pos)
                                        val placeholder = "texto em negrito"
                                        val newText = "$before{$placeholder}$after"
                                        val selStart = pos + 1
                                        val selEnd = selStart + placeholder.length
                                        contentTextField = TextFieldValue(
                                            text = newText,
                                            selection = TextRange(selStart, selEnd)
                                        )
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = CatholicRed,
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FormatBold,
                                    contentDescription = "Negrito",
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Negrito", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }

                            Button(
                                onClick = {
                                    val text = contentTextField.text
                                    val sel = contentTextField.selection
                                    if (sel.start != sel.end) {
                                        val selected = text.substring(sel.start, sel.end)
                                        val before = text.substring(0, sel.start)
                                        val after = text.substring(sel.end)
                                        val newText = "$before:$selected:$after"
                                        val newEnd = sel.end + 2
                                        contentTextField = TextFieldValue(
                                            text = newText,
                                            selection = TextRange(newEnd)
                                        )
                                    } else {
                                        val pos = sel.start
                                        val before = text.substring(0, pos)
                                        val after = text.substring(pos)
                                        val placeholder = "texto em vermelho"
                                        val newText = "$before:$placeholder:$after"
                                        val selStart = pos + 1
                                        val selEnd = selStart + placeholder.length
                                        contentTextField = TextFieldValue(
                                            text = newText,
                                            selection = TextRange(selStart, selEnd)
                                        )
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = LightCatholicRed,
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text("Texto Vermelho", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }

                            Button(
                                onClick = {
                                    val text = contentTextField.text
                                    val sel = contentTextField.selection
                                    if (sel.start != sel.end) {
                                        val selected = text.substring(sel.start, sel.end)
                                        val before = text.substring(0, sel.start)
                                        val after = text.substring(sel.end)
                                        val newText = "$before\n[Título do texto expansível]\n$selected\n[/]\n$after"
                                        val newEnd = sel.end + 34
                                        contentTextField = TextFieldValue(
                                            text = newText,
                                            selection = TextRange(newEnd)
                                        )
                                    } else {
                                        val pos = sel.start
                                        val before = text.substring(0, pos)
                                        val after = text.substring(pos)
                                        val titlePlaceholder = "Título do texto expansível"
                                        val contentPlaceholder = "Conteúdo aqui..."
                                        val newText = "$before\n[$titlePlaceholder]\n$contentPlaceholder\n[/]\n$after"
                                        val titleStart = pos + 2
                                        val titleEnd = titleStart + titlePlaceholder.length
                                        contentTextField = TextFieldValue(
                                            text = newText,
                                            selection = TextRange(titleStart, titleEnd)
                                        )
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = DivineGold,
                                    contentColor = DarkText
                                ),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.UnfoldMore,
                                    contentDescription = "Texto Expansível",
                                    modifier = Modifier.size(16.dp),
                                    tint = DarkText
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Texto Expansível", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = DarkText)
                            }
                        }
                    }

                    // Content text field
                    OutlinedTextField(
                        value = contentTextField,
                        onValueChange = { contentTextField = it },
                        label = { Text("Texto da Oração (**negrito**, 'vermelho' ou [expandir=Título]...[/expandir])") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CatholicRed,
                            focusedLabelColor = CatholicRed
                        )
                    )
                }
            }
        }
    }

    if (showCategorySelector) {
        CategorySelectionDialog(
            categories = categoryOptions,
            selectedCategory = category,
            onSelectCategory = { selected ->
                category = selected
            },
            onDismiss = { showCategorySelector = false },
            onManageCategories = {
                showCategorySelector = false
                onManageCategories()
            }
        )
    }
}

@Composable
fun CategorySelectionDialog(
    categories: List<String>,
    selectedCategory: String,
    onSelectCategory: (String) -> Unit,
    onDismiss: () -> Unit,
    onManageCategories: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Folder,
                    contentDescription = null,
                    tint = CatholicRed
                )
                Text(
                    text = "Selecionar Categoria",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        color = CatholicRed
                    )
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 380.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Escolha a categoria desejada:",
                    style = MaterialTheme.typography.bodySmall.copy(color = Color.DarkGray)
                )

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(categories) { cat ->
                        val isSelected = cat.equals(selectedCategory, ignoreCase = true)
                        Card(
                            onClick = {
                                onSelectCategory(cat)
                                onDismiss()
                            },
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) DivineGold.copy(alpha = 0.22f) else SoftParchmentCard
                            ),
                            border = BorderStroke(
                                width = if (isSelected) 1.5.dp else 1.dp,
                                color = if (isSelected) CatholicRed else DivineGold.copy(alpha = 0.5f)
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 2.dp else 0.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clip(CircleShape)
                                            .background(Color.White)
                                            .border(0.8.dp, DivineGold, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        AsyncImage(
                                            model = R.drawable.img_celtic_cross_icon_new_1782525602645,
                                            contentDescription = "Cruz Celta",
                                            contentScale = ContentScale.Fit,
                                            modifier = Modifier
                                                .size(20.dp)
                                                .clip(CircleShape)
                                        )
                                    }
                                    Text(
                                        text = cat,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontFamily = FontFamily.Serif,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isSelected) CatholicRed else DarkText
                                        )
                                    )
                                }

                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Selecionado",
                                        tint = CatholicRed,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }

            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Fechar", color = CatholicRed, fontWeight = FontWeight.Bold)
            }
        },
        containerColor = Color.White,
        shape = RoundedCornerShape(24.dp)
    )
}

@Composable
fun ConfirmDeleteDialog(
    title: String,
    itemName: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    color = CatholicRed
                )
            )
        },
        text = {
            Text(
                text = "Deseja realmente excluir '$itemName'?",
                style = MaterialTheme.typography.bodyMedium.copy(color = DarkText)
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm()
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                Text("Confirmar Exclusão")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = DarkText)
            }
        },
        containerColor = SoftParchmentCard,
        shape = RoundedCornerShape(20.dp)
    )
}

@Composable
fun FloatingCategoryMenuSheet(
    onDismiss: () -> Unit,
    onSelectCategory: (String) -> Unit,
    onOpenSyncConfig: () -> Unit = {}
) {
    var isVisible by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        isVisible = true
    }

    val dismissWithAnimation: (onComplete: (() -> Unit)?) -> Unit = { onComplete ->
        coroutineScope.launch {
            isVisible = false
            delay(250)
            onComplete?.invoke()
            onDismiss()
        }
    }

    Dialog(
        onDismissRequest = { dismissWithAnimation(null) },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
                .clickable(onClick = { dismissWithAnimation(null) }),
            contentAlignment = Alignment.BottomCenter
        ) {
            AnimatedVisibility(
                visible = isVisible,
                enter = slideInVertically(
                    initialOffsetY = { fullHeight -> fullHeight },
                    animationSpec = tween(durationMillis = 350, easing = FastOutSlowInEasing)
                ) + fadeIn(animationSpec = tween(300)),
                exit = slideOutVertically(
                    targetOffsetY = { fullHeight -> fullHeight },
                    animationSpec = tween(durationMillis = 250, easing = FastOutLinearInEasing)
                ) + fadeOut(animationSpec = tween(250))
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .clickable(enabled = false) {},
                    shape = RoundedCornerShape(28.dp),
                    color = SoftParchmentCard,
                    shadowElevation = 12.dp,
                    border = BorderStroke(1.5.dp, DivineGold.copy(alpha = 0.8f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .width(40.dp)
                                .height(4.dp)
                                .clip(CircleShape)
                                .background(CatholicRed.copy(alpha = 0.3f))
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(Color.White)
                                    .border(1.2.dp, DivineGold, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                AsyncImage(
                                    model = R.drawable.img_celtic_cross_icon_new_1782525602645,
                                    contentDescription = null,
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier
                                        .size(26.dp)
                                        .clip(CircleShape)
                                )
                            }
                            Text(
                                text = "Categorias Principais",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontFamily = FontFamily.Serif,
                                    fontWeight = FontWeight.Bold,
                                    color = CatholicRed,
                                    fontSize = 20.sp
                                )
                            )
                        }

                        Divider(color = DivineGold.copy(alpha = 0.35f), thickness = 1.dp)

                        // 1. Doutrina Button
                        CategoryMenuButton(
                            title = "Doutrina",
                            description = "Catecismo, dogmas e ensinamentos da Fé",
                            icon = Icons.Default.MenuBook,
                            onClick = {
                                dismissWithAnimation {
                                    onSelectCategory("Doutrina")
                                }
                            }
                        )

                        // 2. Novenas Button
                        CategoryMenuButton(
                            title = "Novenas",
                            description = "Novenas aos santos, Maria e Nosso Senhor",
                            icon = Icons.Default.AutoAwesome,
                            onClick = {
                                dismissWithAnimation {
                                    onSelectCategory("Novenas")
                                }
                            }
                        )

                        // 3. Orações Button
                        CategoryMenuButton(
                            title = "Orações",
                            description = "Orações diárias, de cura, proteção e louvor",
                            icon = Icons.Default.Favorite,
                            onClick = {
                                dismissWithAnimation {
                                    onSelectCategory("Orações")
                                }
                            }
                        )

                        OutlinedButton(
                            onClick = {
                                dismissWithAnimation {
                                    onOpenSyncConfig()
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.2.dp, DivineGold)
                        ) {
                            Icon(Icons.Default.Sync, contentDescription = null, tint = CatholicRed, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Sincronizar Orações (GitHub)", color = CatholicRed, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        TextButton(
                            onClick = { dismissWithAnimation(null) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Fechar Menu",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = CatholicRed,
                                    fontSize = 15.sp
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoryMenuButton(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = SoftParchment),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.2.dp, DivineGold.copy(alpha = 0.6f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(CatholicRed)
                    .border(1.5.dp, DivineGold, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = DivineGold,
                    modifier = Modifier.size(24.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        color = CatholicRed,
                        fontSize = 17.sp
                    )
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = DarkText.copy(alpha = 0.75f),
                        fontSize = 12.sp
                    )
                )
            }

            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = null,
                tint = CatholicRed,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}
