# Feature: Crear reserva (sin curso alternativo)
# Técnica: clases de equivalencia sobre los campos del formulario
#
# Clases de equivalencia identificadas:
#
#   Campo nombre del cliente:
#     1 (válida)    – nombre no vacío
#     2 (inválida)  – nombre vacío
#
#   Campo móvil:
#     3 (válida)    – móvil no vacío
#     4 (válida)    - móvil compuesto únicamente por caracteres numéricos
#     5 (inválida)  – móvil vacío
#     6 (inválida)  - móvil compuesto por al menos un carácter no numérico
#
#   Fechas de recogida y devolución:
#     7 (válida)    - fechaRecogida <= fechaDevolucion
#     8 (válida)    - fechaRecogida >= fecha actual
#     10 (inválida) - fechaRecogida > fechaDevolucion
#     11 (inválida) - fechaRecogida < fecha actual
#
#   Horarios de recogida y devolución:
#     13 (válida)   - horaRecogida = true
#     14 (válida)   - horaRecogida = false
#     15 (válida)   - si fechaRecogida == fechaDevolucion, horaRecogida = false
#     16 (válida)   - horaDevolucion = true
#     17 (válida)   - horaDevolucion = false
#     18 (válida)   - si fechaRecogida == fechaDevolucion, horaDevolucion = true
#     19 (inválida) - si fechaRecogida == fechaDevolucion, horaRecogida = true
#     20 (inválida) - si fechaRecogida == fechaDevolucion, horaDevolucion = false
#
#   Quads seleccionados:
#     21 (válida)   - ningún quad seleccionado
#     22 (válida)   - al menos un quad seleccionado
#
#   Acción final:
#     23 (válida)  – usuario confirma la reserva
#     24 (válida)  – usuario cancela el formulario en primera pantalla
#     25 (válida)  - usuario cancela el formulario desde última pantalla
#
# Tabla de casos de prueba:
#   CP | Nombre     | Teléfono    | Fechas                          | Hora recogida | Hora devolución   | Quads | Acción    | Resultado esperado
#   1  | "Client 1" | "612345678" | fechaRecogida < fechaDevolucion | true          | false             | 0     | confirmar | reserva creada
#   2  | "Client 2" | "612345678" | fechaRecogida < fechaDevolucion | false         | true              | 1     | confirmar | reserva creada
#   3  | "Client 3" | "612345678" | fechaRecogida = fechaDevolucion | false         | true              | 1     | confirmar | reserva creada
#   4  | ""         | "612345678" | fechaRecogida < fechaDevolucion | true          | false             | -     | –         | permanece en el formulario
#   5  | "Client 5" | ""          | fechaRecogida < fechaDevolucion | true          | false             | -     | –         | permanece en el formulario
#   6  | "Client 6" | "612ABC678" | fechaRecogida < fechaDevolucion | true          | false             | -     | –         | permanece en el formulario
#   7  | "Client 7" | "612345678" | fechaPasadaRec < fechaDevolucion| true          | false             | -     | –         | permanece en el formulario
#   8  | "Client 8" | "612345678" | fechaRecogida > fechaDevolucion | true          | false             | -     | –         | permanece en el formulario
#   9  | "Client 9" | "612345678" | fechaRecogida = fechaDevolucion | true          | true              | -     | –         | permanece en el formulario
#   10 | "Client 10"| "612345678" | fechaRecogida = fechaDevolucion | false         | false             | -     | –         | permanece en el formulario
#   11 | "Client 11"| "612345678" | -                               | -             | -                 | -     | cancelar_1| vuelve al listado sin nueva reserva
#   12 | "Client 12"| "612345678" | fechaRecogida < fechaDevolucion | true          | false             | 1     | cancelar  | vuelve al listado sin nueva reserva

Feature: Crear reserva
  Como propietario de la empresa de alquiler
  Quiero poder crear nuevas reservas
  Para registrar los alquileres de quads

  Background:
    Given Abro la aplicación de gestión de quads
    And Accedo a la sección de reservas

  # reservas completas y válidas
  Scenario: Crear una reserva válida con recogida por la tarde, devolución por la mañana y 0 quads seleccionados
    When Pulso el botón de nueva reserva
    And Introduzco nombre de cliente "Client 1" y móvil "612345678" en reserva
    And Introduzco fecha de recogida en 1 días y devolución en 3 días
    And Introduzco hora de recogida "true" y hora de devolución "false"
    And Pulso continuar para seleccionar quads
    And Confirmo la selección de quads
    And Confirmo la reserva en la pantalla de confirmación
    Then Hay una reserva nueva en el listado de reservas

  Scenario: Crear una reserva válida con recogida por la mañana, devolución por la tarde y 1 quad seleccionado
    When Pulso el botón de nueva reserva
    And Introduzco nombre de cliente "Client 2" y móvil "612345678" en reserva
    And Introduzco fecha de recogida en 1 días y devolución en 3 días
    And Introduzco hora de recogida "false" y hora de devolución "true"
    And Pulso continuar para seleccionar quads
    And Selecciono el primer quad disponible
    And Confirmo la selección de quads
    And Confirmo la reserva en la pantalla de confirmación
    Then Hay una reserva nueva en el listado de reservas

  Scenario: Crear una reserva válida con mismas fechas, recogida por la mañana, devolución por la tarde y 1 quad seleccionado
    When Pulso el botón de nueva reserva
    And Introduzco nombre de cliente "Client 3" y móvil "612345678" en reserva
    And Introduzco fecha de recogida en 1 días y devolución en 1 días
    And Introduzco hora de recogida "false" y hora de devolución "true"
    And Pulso continuar para seleccionar quads
    And Selecciono el primer quad disponible
    And Confirmo la selección de quads
    And Confirmo la reserva en la pantalla de confirmación
    Then Hay una reserva nueva en el listado de reservas

  # nombre vacío
  Scenario: Intentar crear una reserva sin nombre de cliente
    When Pulso el botón de nueva reserva
    And Introduzco nombre de cliente "" y móvil "612345678" en reserva
    And Introduzco fecha de recogida en 1 días y devolución en 3 días
    And Introduzco hora de recogida "true" y hora de devolución "false"
    Then Permanece en el formulario de reserva

  # móvil vacío
  Scenario: Intentar crear una reserva sin móvil
    When Pulso el botón de nueva reserva
    And Introduzco nombre de cliente "Client 5" y móvil "" en reserva
    And Introduzco fecha de recogida en 1 días y devolución en 3 días
    And Introduzco hora de recogida "true" y hora de devolución "false"
    Then Permanece en el formulario de reserva

  # móvil no válido
  Scenario: Intentar crear una reserva con móvil inválido
    When Pulso el botón de nueva reserva
    And Introduzco nombre de cliente "Client 6" y móvil "612ABC678" en reserva
    And Introduzco fecha de recogida en 1 días y devolución en 3 días
    And Introduzco hora de recogida "true" y hora de devolución "false"
    Then Permanece en el formulario de reserva

  # fecha recogida en el pasado
  Scenario: Intentar crear una reserva con fecha de recogida en el pasado
    When Pulso el botón de nueva reserva
    And Introduzco nombre de cliente "Client 7" y móvil "612345678" en reserva
    And Introduzco fecha de recogida en -2 días y devolución en 1 días
    And Introduzco hora de recogida "true" y hora de devolución "false"
    Then Permanece en el formulario de reserva

  # fecha devolución anterior a recogida
  Scenario: Intentar crear una reserva con fecha de devolución anterior a la recogida
    When Pulso el botón de nueva reserva
    And Introduzco nombre de cliente "Client 8" y móvil "612345678" en reserva
    And Introduzco fecha de recogida en 3 días y devolución en 1 días
    And Introduzco hora de recogida "true" y hora de devolución "false"
    Then Permanece en el formulario de reserva

  # mismas fechas, hora recogida por la tarde
  Scenario: Intentar crear una reserva con misma fecha y hora de recogida por la tarde
    When Pulso el botón de nueva reserva
    And Introduzco nombre de cliente "Client 9" y móvil "612345678" en reserva
    And Introduzco fecha de recogida en 1 días y devolución en 1 días
    And Introduzco hora de recogida "true" y hora de devolución "true"
    Then Permanece en el formulario de reserva

  # mismas fechas, hora devolución por la mañana
  Scenario: Intentar crear una reserva con misma fecha y hora de devolución por la mañana
    When Pulso el botón de nueva reserva
    And Introduzco nombre de cliente "Client 10" y móvil "612345678" en reserva
    And Introduzco fecha de recogida en 1 días y devolución en 1 días
    And Introduzco hora de recogida "false" y hora de devolución "false"
    Then Permanece en el formulario de reserva

  # cancelar desde primer formulario
  Scenario: Cancelar la creación de una reserva desde el primer formulario
    When Pulso el botón de nueva reserva
    And Introduzco nombre de cliente "Client 11" y móvil "612345678" en reserva
    And Pulso cancelar en el formulario de reserva
    Then Vuelvo al listado de reservas sin reserva nueva

  # cancelar desde último paso de creación
  Scenario: Cancelar la creación de una reserva desde pantalla de confirmación
    When Pulso el botón de nueva reserva
    And Introduzco nombre de cliente "Client 11" y móvil "612345678" en reserva
    And Introduzco fecha de recogida en 1 días y devolución en 3 días
    And Introduzco hora de recogida "false" y hora de devolución "true"
    And Pulso continuar para seleccionar quads
    And Selecciono el primer quad disponible
    And Confirmo la selección de quads
    And Cancelo la reserva en la pantalla de confirmación
    Then Vuelvo al listado de reservas sin reserva nueva
