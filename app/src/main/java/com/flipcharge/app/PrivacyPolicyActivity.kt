package com.flipcharge.app

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flipcharge.app.ui.theme.FlipChargeTheme
import com.flipcharge.app.ui.theme.Manrope
import com.flipcharge.app.ui.theme.SpaceGrotesk

/**
 * Genuine in-app, offline privacy disclosure. Its bilingual legal text follows
 * docs/privacy/index.html / https://rotate180-privacy.vercel.app/.
 *
 * No WebView, Internet permission, JavaScript or data collection is introduced.
 * The public copy remains accessible from within the screen for Play policy.
 */
class PrivacyPolicyActivity : LocalizedActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val systemLanguage = resources.configuration.locales.get(0)?.language
        val configured = AppLocaleManager.currentLanguage(this)
        val initiallyItalian = (configured.takeIf { it.isNotBlank() } ?: systemLanguage) == "it"
        setContent {
            FlipChargeTheme {
                PrivacyPolicyScreen(
                    initiallyItalian = initiallyItalian,
                    onBack = { finish() },
                    onOpenOnline = { lang ->
                        openExternal(PRIVACY_URL + "?lang=" + lang)
                    },
                    onContact = {
                        openExternal(CONTACT_URL)
                    },
                )
            }
        }
    }

    private fun openExternal(url: String) {
        runCatching {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
        }
    }

    companion object {
        const val PRIVACY_URL = "https://rotate180-privacy.vercel.app/"
        private const val CONTACT_URL = "https://github.com/riccardopinato/180FlipCharge/issues/new"
    }
}

private data class PolicySection(
    val title: String,
    val paragraphs: List<String>,
)

private data class PolicyCopy(
    val title: String,
    val updated: String,
    val intro: String,
    val sections: List<PolicySection>,
    val online: String,
    val contact: String,
    val note: String,
)

private fun policyCopy(italian: Boolean): PolicyCopy = if (italian) {
    PolicyCopy(
        title = "Informativa sulla privacy",
        updated = "Aggiornamento: 12 novembre 2026 · com.flipcharge.app",
        intro = "Rotate 180° è un'utilità di rotazione dello schermo che funziona localmente sul dispositivo. Non richiede la creazione di un account.",
        sections = listOf(
            PolicySection("1. Titolare e contatti", listOf(
                "Progetto e pubblicatore indicato per questa applicazione: riccardopinato (repository App Factory). Per domande e richieste relative alla privacy è disponibile il canale di contatto del progetto su GitHub. Per inviare una richiesta potrebbe essere necessario un account GitHub.",
                "Prima della distribuzione sul Play Store, il nome del titolare e il contatto dovranno essere allineati al profilo sviluppatore effettivo."
            )),
            PolicySection("2. Informazioni utilizzate sul dispositivo", listOf(
                "• Preferenze di rotazione: attivazione manuale, modalità Auto 180°, ricarica e lingua scelta.",
                "• Elenco delle app avviabili: visibile nella sezione Eccezioni app per selezionare le app da escludere.",
                "• Informazioni di utilizzo delle app: se autorizzato, l'app verifica quale app è in primo piano per applicare le eccezioni selezionate.",
                "• Stato del servizio e della ricarica: utilizzato per mostrare lo stato dell'orientamento e far funzionare le automazioni."
            )),
            PolicySection("3. Permessi Android e finalità", listOf(
                "• Visualizzazione sopra altre app (Overlay): impiegata per applicare l'orientamento richiesto con un elemento non interattivo.",
                "• Accesso ai dati di utilizzo: serve a riconoscere le app da escludere dalla rotazione; è facoltativo per l'uso senza eccezioni.",
                "• Servizio in primo piano e notifiche: mantengono la funzione scelta mentre l'app non è aperta; la notifica offre il comando STOP.",
                "• Avvio completato: permette di tentare il ripristino dei controlli abilitati dopo un riavvio, nei limiti imposti da Android."
            )),
            PolicySection("4. Trasmissione, pubblicità e condivisione", listOf(
                "La versione Android esaminata non include un account, SDK pubblicitari o moduli di analisi d'uso e non dichiara il permesso Internet nel Manifest dell'app. Le preferenze e i dati di utilizzo delle app sono elaborati localmente e non vengono inviati a un server gestito dallo sviluppatore.",
                "Questa descrizione riguarda l'applicazione; il sistema operativo e i servizi Google hanno proprie informative."
            )),
            PolicySection("5. Backup del sistema operativo", listOf(
                "L'app autorizza i meccanismi di backup Android. Secondo le impostazioni del dispositivo e del servizio di backup, Android può salvare o trasferire preferenze dell'app, comprese le scelte sulle eccezioni, verso un dispositivo ripristinato.",
                "Il backup è gestito dal sistema operativo, non da un server dell'app. Puoi gestirlo dalle impostazioni Android e del tuo account di backup."
            )),
            PolicySection("6. Conservazione ed eliminazione", listOf(
                "Le impostazioni e le eccezioni rimangono sul dispositivo finché non vengono modificate, cancellati i dati dell'app o disinstallata l'app.",
                "Gli eventuali backup effettuati dal sistema possono essere soggetti alle politiche di conservazione del fornitore del backup. L'app non offre un account da eliminare."
            )),
            PolicySection("7. Modifiche", listOf(
                "Se cambieranno permessi, flussi di dati o modalità di conservazione, questa informativa verrà aggiornata prima della distribuzione della versione interessata."
            ))
        ),
        online = "Apri la versione online",
        contact = "Contatti privacy",
        note = "Disponibile offline. La versione pubblica online resta consultabile separatamente.",
    )
} else {
    PolicyCopy(
        title = "Privacy Policy",
        updated = "Last updated: November 12, 2026 · com.flipcharge.app",
        intro = "Rotate 180° is an on-device screen-orientation utility. It does not require account creation.",
        sections = listOf(
            PolicySection("1. Publisher and contact", listOf(
                "Project and developer identifier: riccardopinato (App Factory repository). For privacy questions or requests, use the project contact channel on GitHub. Submitting a request may require a GitHub account.",
                "Before Google Play distribution, the controller identity and contact will be aligned with the actual developer profile."
            )),
            PolicySection("2. Information accessed on your device", listOf(
                "• Rotation settings: manual toggle, Auto 180°, charger automation and selected language.",
                "• List of launchable apps: displayed to let users select app-specific rotation exclusions.",
                "• App usage information: if Usage Access permission is granted, the app checks which app is in the foreground to apply user-selected exclusions.",
                "• Service and charging states: used to show runtime status and enable automatic behavior."
            )),
            PolicySection("3. Android permissions and purposes", listOf(
                "• Display over other apps: used to request device orientation through a non-interactive overlay.",
                "• Usage Access: detects selected excluded apps; optional if no exclusions are used.",
                "• Foreground service and notifications: maintain selected orientation behavior when the UI is closed, with a STOP action.",
                "• Boot completed: attempts to restore enabled controls after a reboot, subject to Android restrictions."
            )),
            PolicySection("4. Data transfer, advertising and sharing", listOf(
                "The reviewed Android app has no account, ad SDKs or analytics modules, and does not declare Internet permission in its app Manifest. App preferences and usage observations are processed locally and not transmitted to a developer-operated server.",
                "This describes the app itself; the operating system and Google services have their own privacy policies."
            )),
            PolicySection("5. Operating system backup", listOf(
                "The app permits Android system backup. Depending on device and backup settings, Android may back up or transfer app preferences, including exclusion choices, when restoring a device.",
                "These backups are managed by the operating system and not by an app server. Manage them through Android and backup provider settings."
            )),
            PolicySection("6. Retention and deletion", listOf(
                "Settings and exclusions remain on-device until modified, app data is cleared, or the app is uninstalled.",
                "Any OS-managed backup copies are subject to the backup provider's retention policies. There is no app account to delete."
            )),
            PolicySection("7. Updates", listOf(
                "If permissions, data handling or retention change, this notice will be updated before the affected app version is distributed."
            ))
        ),
        online = "Open public online policy",
        contact = "Privacy contact",
        note = "Available offline. The public online policy remains separately accessible.",
    )
}

@Composable
private fun PrivacyPolicyScreen(
    initiallyItalian: Boolean,
    onBack: () -> Unit,
    onOpenOnline: (String) -> Unit,
    onContact: () -> Unit,
) {
    var italian by rememberSaveable { mutableStateOf(initiallyItalian) }
    val copy = policyCopy(italian)
    val panel = RoundedCornerShape(21.dp)
    Scaffold(containerColor = Color(0xFF0B1015)) { insets ->
        LazyColumn(
            modifier = Modifier
                .background(
                    Brush.verticalGradient(listOf(Color(0xFF132329), Color(0xFF0B1015))),
                )
                .padding(insets),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 40.dp),
            verticalArrangement = Arrangement.spacedBy(15.dp),
        ) {
            item {
                TextButton(onClick = onBack, contentPadding = PaddingValues(0.dp)) {
                    Text("← " + if (italian) "Impostazioni" else "Settings")
                }
                Spacer(modifier = Modifier.size(4.dp))
                Text(
                    "PRIVACY / DOCUMENT",
                    style = MaterialTheme.typography.labelMedium,
                    letterSpacing = 1.4.sp,
                    color = Color(0xFF80D6C7),
                )
                Text(
                    copy.title,
                    fontFamily = SpaceGrotesk,
                    fontSize = 27.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFEAF1F1),
                    modifier = Modifier.padding(top = 8.dp, bottom = 4.dp),
                )
                Text(copy.updated, fontFamily = Manrope, fontSize = 11.sp, color = Color(0xFF9EB6BA))
            }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    LanguageChip("Italiano", selected = italian, onClick = { italian = true }, modifier = Modifier.weight(1f))
                    LanguageChip("English", selected = !italian, onClick = { italian = false }, modifier = Modifier.weight(1f))
                }
            }
            item {
                Card(
                    shape = panel,
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1B302F)),
                    modifier = Modifier.fillMaxWidth().border(1.dp, Color(0xFF3F8074), panel),
                ) {
                    Text(
                        copy.intro,
                        modifier = Modifier.padding(19.dp),
                        fontFamily = Manrope,
                        fontSize = 14.sp,
                        lineHeight = 22.sp,
                        color = Color(0xFFE3F1ED),
                    )
                }
            }
            items(copy.sections.size) { index ->
                val section = copy.sections[index]
                Card(
                    shape = panel,
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF17252C)),
                    modifier = Modifier.fillMaxWidth().border(1.dp, Color(0xFF3A555B), panel),
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(11.dp),
                    ) {
                        Text(
                            section.title,
                            fontFamily = SpaceGrotesk,
                            fontSize = 17.sp,
                            lineHeight = 23.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFFEAF1F1),
                        )
                        section.paragraphs.forEach { paragraph ->
                            Text(
                                paragraph,
                                fontFamily = Manrope,
                                fontSize = 13.sp,
                                lineHeight = 21.sp,
                                color = Color(0xFFB5C8C9),
                            )
                        }
                        if (index == 0) {
                            OutlinedButton(
                                onClick = onContact,
                                modifier = Modifier.fillMaxWidth(),
                            ) { Text(copy.contact) }
                        }
                    }
                }
            }
            item {
                Button(
                    onClick = { onOpenOnline(if (italian) "it" else "en") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1B776B),
                        contentColor = Color.White,
                    ),
                    contentPadding = PaddingValues(vertical = 14.dp),
                ) {
                    Text(copy.online, fontFamily = Manrope, fontWeight = FontWeight.Bold)
                }
                Text(
                    copy.note,
                    modifier = Modifier.padding(top = 12.dp),
                    fontFamily = Manrope,
                    fontSize = 11.sp,
                    lineHeight = 17.sp,
                    color = Color(0xFF90A9AB),
                )
            }
        }
    }
}

@Composable
private fun LanguageChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(12.dp)
    OutlinedButton(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = if (selected) Color(0xFF1C5A53) else Color(0xFF17252C),
            contentColor = if (selected) Color(0xFFE5FDF8) else Color(0xFFADBCBD),
        ),
        shape = shape,
    ) {
        Text(text, fontFamily = Manrope, fontSize = 12.sp, fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium)
    }
}
