# Feature: Enviar reserva (sin curso alternativo)

Feature: Enviar reserva
  Como propietario de la empresa de alquiler
  Quiero poder confirmar o descartar una reserva desde la pantalla de confirmación
  Para guardar solo las reservas que realmente quiero registrar

  Background:
    Given Abro la aplicación de gestión de quads
    And Accedo a la sección de reservas

  # confirmar reserva
  Scenario: Confirmar el envío de una reserva desde la pantalla de confirmación
    When Pulso sobre la primera reserva del listado para ver su detalle
    And Pulso el botón enviar en el detalle de la reserva

