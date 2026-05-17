# Feature: Enviar reserva (sin curso alternativo)

Feature: Enviar reserva
  Como propietario de la empresa de alquiler
  Quiero poder enviar los datos de una reserva desde su detalle
  Para comunicar la información de la reserva al cliente

  Background:
    Given Abro la aplicación de gestión de quads
    And Accedo a la sección de reservas
    And Existe al menos una reserva en el listado

  Scenario: Enviar una reserva desde la pantalla de detalle
    When Pulso sobre la primera reserva del listado para ver su detalle
    And Pulso el botón enviar en el detalle de la reserva
    Then Se lanza la acción de envío de la reserva