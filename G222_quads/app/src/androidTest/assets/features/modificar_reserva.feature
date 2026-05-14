# Feature: Modificar reserva (sin curso alternativo)
# Técnica: clases de equivalencia sobre los campos editables
#
# Clases de equivalencia identificadas:
#
#   Campo nombre del cliente:
#     1 (válida)   – nombre no vacío
#     2 (inválida) – nombre vacío
#
#   Campo teléfono:
#     3 (válida)   – teléfono no vacío
#     4 (inválida) – teléfono vacío
#
#   Fechas:
#     5 (válida)   – fecha recogida < fecha devolución
#     6 (inválida) – fecha devolución <= fecha recogida
#
#   Acción:
#     7 (válida)   – usuario guarda los cambios
#     8 (válida)   – usuario cancela (sin cambios)
#
# Tabla de casos de prueba:
#   CP | Nombre     | Teléfono    | Fechas           | Acción    | Resultado esperado
#   1  | "Client 2" | "699000001" | recogida < dev.  | guardar   | reserva actualizada
#   2  | ""         | "699000001" | recogida < dev.  | –         | botón continuar deshabilitado
#   3  | "Client 2" | ""          | recogida < dev.  | –         | botón continuar deshabilitado
#   4  | "Client 2" | "699000001" | dev. <= recogida | –         | botón continuar deshabilitado
#   5  | "Client 2" | "699000001" | recogida < dev.  | cancelar  | reserva sin cambios

Feature: Modificar reserva
  Como propietario de la empresa de alquiler
  Quiero poder modificar los datos de una reserva existente
  Para corregir errores o actualizar información

  Background:
    Given Abro la aplicación de gestión de quads
    And Accedo a la sección de reservas
    And Existe al menos una reserva en el listado

  # modificación válida
  Scenario: Modificar una reserva con datos correctos
    When Pulso editar en la primera reserva del listado
    And Cambio el nombre a "Client 2" y el teléfono a "699000001" en reserva
    And Introduzco fecha de recogida en 1 días y devolución en 3 días
    And Pulso continuar para seleccionar quads
    And Selecciono el primer quad disponible
    And Confirmo la selección de quads
    And Confirmo la reserva en la pantalla de confirmación
    Then La reserva aparece en el listado con el nombre "Client 2"

  # nombre vacío al modificar
  Scenario: Intentar modificar una reserva dejando el nombre vacío
    When Pulso editar en la primera reserva del listado
    And Cambio el nombre a "" y el teléfono a "699000001" en reserva
    And Introduzco fecha de recogida en 1 días y devolución en 3 días
    Then El botón continuar de la reserva está deshabilitado

  # teléfono vacío al modificar
  Scenario: Intentar modificar una reserva dejando el teléfono vacío
    When Pulso editar en la primera reserva del listado
    And Cambio el nombre a "Client 2" y el teléfono a "" en reserva
    And Introduzco fecha de recogida en 1 días y devolución en 3 días
    Then El botón continuar de la reserva está deshabilitado

  # fechas inválidas al modificar
  Scenario: Intentar modificar una reserva con fecha de devolución anterior a la recogida
    When Pulso editar en la primera reserva del listado
    And Cambio el nombre a "Client 2" y el teléfono a "699000001" en reserva
    And Introduzco fecha de recogida en 3 días y devolución en 1 días
    Then El botón continuar de la reserva está deshabilitado

  # cancelar modificación
  Scenario: Cancelar la modificación de una reserva
    When Pulso editar en la primera reserva del listado
    And Cambio el nombre a "No debe guardarse" y el teléfono a "699000001" en reserva
    And Pulso cancelar en el formulario de reserva
    Then La reserva no muestra el nombre "No debe guardarse" en el listado
