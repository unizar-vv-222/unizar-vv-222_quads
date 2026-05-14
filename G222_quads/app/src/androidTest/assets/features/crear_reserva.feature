# Feature: Crear reserva (sin curso alternativo)
# Técnica: clases de equivalencia sobre los campos del formulario
#
# Clases de equivalencia identificadas:
#
#   Campo nombre del cliente:
#     CE1 (válida)   – nombre no vacío
#     CE2 (inválida) – nombre vacío
#
#   Campo teléfono:
#     CE3 (válida)   – teléfono no vacío
#     CE4 (inválida) – teléfono vacío
#
#   Fechas:
#     CE5 (válida)   – fecha recogida < fecha devolución, ambas futuras
#     CE6 (inválida) – fecha devolución <= fecha recogida
#     CE7 (inválida) – fecha recogida en el pasado
#
#   Selección de quads:
#     CE8 (válida)   – al menos un quad seleccionado
#     CE9 (inválida) – ningún quad seleccionado (confirmar sin marcar)
#
#   Acción final:
#     CE10 (válida)  – usuario confirma la reserva
#     CE11 (válida)  – usuario cancela el formulario (sin reserva)
#
# Tabla de casos de prueba:
#   CP | Nombre     | Teléfono    | Fechas            | Quads     | Acción    | Resultado esperado
#   1  | "Client 1" | "612345678" | recogida < dev.   | 1 selec.  | confirmar | reserva creada (CE1,CE3,CE5,CE8,CE10)
#   2  | ""         | "612345678" | recogida < dev.   | –         | –         | botón continuar deshabilitado (CE2)
#   3  | "Client 1" | ""          | recogida < dev.   | –         | –         | botón continuar deshabilitado (CE4)
#   4  | "Client 1" | "612345678" | dev. <= recogida  | –         | –         | botón continuar deshabilitado (CE6)
#   5  | "Client 1" | "612345678" | recogida pasada   | –         | –         | botón continuar deshabilitado (CE7)
#   6  | "Client 1" | "612345678" | recogida < dev.   | 0 selec.  | confirmar | botón confirmar deshabilitado (CE9)
#   7  | "Client 1" | "612345678" | recogida < dev.   | –         | cancelar  | vuelve al listado sin nueva reserva (CE11)

Feature: Crear reserva
  Como propietario de la empresa de alquiler
  Quiero poder crear nuevas reservas
  Para registrar los alquileres de quads

  Background:
    Given Abro la aplicación de gestión de quads
    And Accedo a la sección de reservas

  # reserva completa y válida
  Scenario: Crear una reserva con todos los datos correctos
    When Pulso el botón de nueva reserva
    And Introduzco nombre de cliente "Client 1" y teléfono "612345678" en reserva
    And Introduzco fecha de recogida en 1 días y devolución en 3 días
    And Pulso continuar para seleccionar quads
    And Selecciono el primer quad disponible
    And Confirmo la selección de quads
    And Confirmo la reserva en la pantalla de confirmación
    Then La reserva de "Client 1" aparece en el listado de reservas

  # nombre vacío
  Scenario: Intentar crear una reserva sin nombre de cliente
    When Pulso el botón de nueva reserva
    And Introduzco nombre de cliente "" y teléfono "612345678" en reserva
    And Introduzco fecha de recogida en 1 días y devolución en 3 días
    Then El botón continuar de la reserva está deshabilitado

  # teléfono vacío
  Scenario: Intentar crear una reserva sin teléfono
    When Pulso el botón de nueva reserva
    And Introduzco nombre de cliente "Client 1" y teléfono "" en reserva
    And Introduzco fecha de recogida en 1 días y devolución en 3 días
    Then El botón continuar de la reserva está deshabilitado

  # fecha devolución anterior a recogida
  Scenario: Intentar crear una reserva con fecha de devolución anterior a la recogida
    When Pulso el botón de nueva reserva
    And Introduzco nombre de cliente "Client 1" y teléfono "612345678" en reserva
    And Introduzco fecha de recogida en 3 días y devolución en 1 días
    Then El botón continuar de la reserva está deshabilitado

  # fecha recogida en el pasado
  Scenario: Intentar crear una reserva con fecha de recogida en el pasado
    When Pulso el botón de nueva reserva
    And Introduzco nombre de cliente "Client 1" y teléfono "612345678" en reserva
    And Introduzco fecha de recogida en -2 días y devolución en 1 días
    Then El botón continuar de la reserva está deshabilitado

  # sin quad seleccionado
  Scenario: Intentar confirmar una reserva sin seleccionar ningún quad
    When Pulso el botón de nueva reserva
    And Introduzco nombre de cliente "Client 1" y teléfono "612345678" en reserva
    And Introduzco fecha de recogida en 1 días y devolución en 3 días
    And Pulso continuar para seleccionar quads
    Then El botón confirmar de la selección de quads está deshabilitado

  # cancelar formulario
  Scenario: Cancelar la creación de una reserva desde el formulario
    When Pulso el botón de nueva reserva
    And Introduzco nombre de cliente "Client 1" y teléfono "612345678" en reserva
    And Pulso cancelar en el formulario de reserva
    Then Vuelvo al listado de reservas sin reserva nueva
