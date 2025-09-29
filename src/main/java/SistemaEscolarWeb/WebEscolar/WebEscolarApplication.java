package SistemaEscolarWeb.WebEscolar;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan; // 👈 NOVA IMPORTAÇÃO

@SpringBootApplication
@ComponentScan({
    "controllers",          // 👈 Inclui o pacote dos Services (AlunoService, ProfessorService)
    "models",               // 👈 Inclui o pacote dos Models (Aluno, Professor)
    "com.paulo.escolar.controllers" // 👈 Se você colocou os novos controllers neste pacote.
    // Se seus novos controllers (LoginController, AlunoController) estiverem no pacote 'controllers'
    // junto com os Services, você só precisa de "controllers". Se estão em um subpacote como 
    // com.paulo.escolar.controllers, use este segundo caminho.
})
public class WebEscolarApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebEscolarApplication.class, args);
    }
}
