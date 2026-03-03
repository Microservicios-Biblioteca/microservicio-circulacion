package co.analisys.biblioteca.controller;

import co.analisys.biblioteca.model.LibroId;
import co.analisys.biblioteca.model.Prestamo;
import co.analisys.biblioteca.model.PrestamoId;
import co.analisys.biblioteca.model.UsuarioId;
import co.analisys.biblioteca.service.CirculacionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/circulacion")
@Tag(name = "Circulación", description = "API para la gestión de préstamos y devoluciones de libros")
public class CirculacionController {
    @Autowired
    private CirculacionService circulacionService;

    @Operation(summary = "Prestar un libro", description = "Registra el préstamo de un libro a un usuario. "
            + "Verifica la disponibilidad del libro, actualiza su estado y envía una notificación. "
            + "Requiere rol ADMIN o BIBLIOTECARIO")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Préstamo registrado exitosamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado - Token JWT requerido"),
            @ApiResponse(responseCode = "403", description = "No autorizado - Se requiere rol ADMIN o BIBLIOTECARIO"),
            @ApiResponse(responseCode = "500", description = "Libro no disponible o error interno")
    })
    @PostMapping("/prestar")
    public void prestarLibro(
            @Parameter(description = "ID del usuario que solicita el préstamo", required = true, example = "1") @RequestParam String usuarioId,
            @Parameter(description = "ID del libro a prestar", required = true, example = "1") @RequestParam String libroId) {
        circulacionService.prestarLibro(new UsuarioId(usuarioId), new LibroId(libroId));
    }

    @Operation(summary = "Devolver un libro", description = "Registra la devolución de un libro prestado. "
            + "Actualiza el estado del préstamo, la disponibilidad del libro y envía una notificación. "
            + "Requiere rol ADMIN o BIBLIOTECARIO")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Devolución registrada exitosamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado - Token JWT requerido"),
            @ApiResponse(responseCode = "403", description = "No autorizado - Se requiere rol ADMIN o BIBLIOTECARIO"),
            @ApiResponse(responseCode = "500", description = "Préstamo no encontrado o error interno")
    })
    @PostMapping("/devolver")
    public void devolverLibro(
            @Parameter(description = "ID del préstamo a devolver", required = true, example = "abc-123-def") @RequestParam String prestamoId) {
        circulacionService.devolverLibro(new PrestamoId(prestamoId));
    }

    @Operation(summary = "Obtener todos los préstamos", description = "Retorna la lista completa de todos los préstamos registrados en el sistema (activos, devueltos y vencidos)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de préstamos obtenida exitosamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado - Token JWT requerido"),
            @ApiResponse(responseCode = "403", description = "No autorizado - Rol insuficiente")
    })
    @GetMapping("/prestamos")
    public List<Prestamo> obtenerTodosPrestamos() {
        return circulacionService.obtenerTodosPrestamos();
    }
}
