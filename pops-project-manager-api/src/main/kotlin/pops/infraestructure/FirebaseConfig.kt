package pops.employee.infrastructure

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import java.io.File
import java.io.FileInputStream

@Configuration
class FirebaseConfig(
    @Value("\${firebase.service-account-path}")
    private val firebaseKeyPath: String
) {

    @PostConstruct
    fun initFirebase() {
        val firebaseKeyFile = File(firebaseKeyPath)
        
        if (!firebaseKeyFile.exists()) {
            println("⚠️ Arquivo Firebase não encontrado em: $firebaseKeyPath")
            println("⚠️ Firebase não será inicializado. A aplicação continuará sem autenticação Firebase.")
            return
        }

        try {
            val serviceAccount = FileInputStream(firebaseKeyFile)

            val options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build()

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options)
                println("✅ Firebase inicializado com sucesso")
            }
        } catch (e: Exception) {
            println("⚠️ Erro ao inicializar Firebase: ${e.message}")
            println("⚠️ A aplicação continuará sem autenticação Firebase.")
        }
    }
}
