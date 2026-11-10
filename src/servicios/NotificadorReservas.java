package servicios;

import modelo.Reserva;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class NotificadorReservas implements ReservaObserver {

    private static final DateTimeFormatter FORMATO_FECHA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Override
    public void reservaCreada(Reserva reserva) {
        System.out.println("[RESERVAS] " + ahora() + " - Reserva #" + reserva.getId()
                + " creada para el asistente " + reserva.getIdAsistente()
                + " en el alojamiento " + reserva.getIdAlojamiento()
                + " del " + reserva.getIngreso() + " al " + reserva.getEgreso() + ".");
    }

    @Override
    public void reservaCancelada(Reserva reserva) {
        System.out.println("[RESERVAS] " + ahora() + " - Reserva #" + reserva.getId()
                + " cancelada (asistente " + reserva.getIdAsistente()
                + ", alojamiento " + reserva.getIdAlojamiento() + ").");
    }

    private String ahora() {
        return LocalDateTime.now().format(FORMATO_FECHA);
    }
}
