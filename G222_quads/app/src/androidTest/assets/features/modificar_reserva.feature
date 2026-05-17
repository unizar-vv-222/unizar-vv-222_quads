# Feature: Modificar reserva (sin curso alternativo)
# Técnica: clases de equivalencia sobre los campos editables
#
# Clases de equivalencia identificadas:
#
#   Precondición (reserva existente):
#     1 (válida)   – reserva existente en el listado
#
#   Origen de la acción:
#     2 (válida)   – eliminación desde el listado (botón en ítem)
#     3 (válida)   – eliminación desde la pantalla de detalle
#
#   Campo nombre del cliente:
#     4 (válida)    – nombre no vacío
#     5 (inválida)  – nombre vacío
#
#   Campo móvil:
#     6 (válida)    – móvil no vacío
#     7 (válida)    - móvil compuesto únicamente por caracteres numéricos
#     8 (inválida)  – móvil vacío
#     9 (inválida)  - móvil compuesto por al menos un carácter no numérico
#
#   Fechas de recogida y devolución:
#     10 (válida)    - fechaRecogida <= fechaDevolucion
#     11 (válida)    - fechaRecogida >= fecha actual
#     12 (inválida) - fechaRecogida > fechaDevolucion
#     13 (inválida) - fechaRecogida < fecha actual
#
#   Horarios de recogida y devolución:
#     14 (válida)   - horaRecogida = true
#     15 (válida)   - horaRecogida = false
#     16 (válida)   - si fechaRecogida == fechaDevolucion, horaRecogida = false
#     17 (válida)   - horaDevolucion = true
#     18 (válida)   - horaDevolucion = false
#     19 (válida)   - si fechaRecogida == fechaDevolucion, horaDevolucion = true
#     20 (inválida) - si fechaRecogida == fechaDevolucion, horaRecogida = true
#     21 (inválida) - si fechaRecogida == fechaDevolucion, horaDevolucion = false
#
#   Quads seleccionados:
#     22 (válida)   - ningún quad seleccionado
#     23 (válida)   - al menos un quad seleccionado
#
#   Acción final:
#     24 (válida)  – usuario confirma la reserva
#     25 (válida)  – usuario cancela el formulario en primera pantalla
#     26 (válida)  - usuario cancela el formulario desde última pantalla
#
# Tabla de casos de prueba:
#   CP | Nombre     | Móvil       | Fechas                          | Hora recogida | Hora devolución   | Quads | Origen    | Acción    | Resultado esperado
#   1  | "Client 1" | "612345678" | fechaRecogida < fechaDevolucion | true          | false             | 0     | Listado   | confirmar | reserva creada
#   2  | "Client 2" | "612345678" | fechaRecogida < fechaDevolucion | false         | true              | 1     | Listado   | confirmar | reserva creada
#   3  | "Client 3" | "612345678" | fechaRecogida = fechaDevolucion | false         | true              | 1     | Detalle   | confirmar | reserva creada
#   4  | ""         | "612345678" | fechaRecogida < fechaDevolucion | true          | false             | -     | Listado   | –         | permanece en el formulario
#   5  | "Client 5" | ""          | fechaRecogida < fechaDevolucion | true          | false             | -     | Listado   | –         | permanece en el formulario
#   6  | "Client 6" | "612ABC678" | fechaRecogida < fechaDevolucion | true          | false             | -     | Listado   | –         | permanece en el formulario
#   7  | "Client 7" | "612345678" | fechaPasadaRec < fechaDevolucion| true          | false             | -     | Listado   | –         | permanece en el formulario
#   8  | "Client 8" | "612345678" | fechaRecogida > fechaDevolucion | true          | false             | -     | Listado   | –         | permanece en el formulario
#   9  | "Client 9" | "612345678" | fechaRecogida = fechaDevolucion | true          | true              | -     | Listado   | –         | permanece en el formulario
#   10 | "Client 10"| "612345678" | fechaRecogida = fechaDevolucion | false         | false             | -     | Listado   | –         | permanece en el formulario
#   11 | "Client 11"| "612345678" | -                               | -             | -                 | -     | Listado   | cancelar_1| vuelve al listado sin nueva reserva
#   12 | "Client 12"| "612345678" | fechaRecogida < fechaDevolucion | true          | false             | 1     | Listado   | cancelar  | vuelve al listado sin nueva reserva

Feature: Modificar reserva
  Como propietario de la empresa de alquiler
  Quiero poder modificar los datos de una reserva existente
  Para corregir errores o actualizar información

  Background:
    Given Abro la aplicación de gestión de quads
    And Accedo a la sección de reservas
    And Existe al menos una reserva en el listado

  # modificaciones válidas
  Scenario: Modificar una reserva válida desde listado con recogida por la tarde, devolución por la mañana y 0 quads seleccionados
    When Pulso editar en la primera reserva del listado
    And Cambio el nombre a "Client 1" y el móvil a "612345678" en reserva
    And Introduzco fecha de recogida en 1 días y devolución en 3 días
    And Introduzco hora de recogida "true" y hora de devolución "false"
    And Pulso continuar para seleccionar quads
    And Confirmo la selección de quads
    And Confirmo la reserva en la pantalla de confirmación
    Then La reserva seleccionada muestra en detalle el nombre "Client 1" y el móvil "612345678"

  Scenario: Modificar una reserva válida desde listado con recogida por la mañana, devolución por la tarde y 1 quad seleccionado
    When Pulso editar en la primera reserva del listado
    And Cambio el nombre a "Client 2" y el móvil a "612345678" en reserva
    And Introduzco fecha de recogida en 1 días y devolución en 3 días
    And Introduzco hora de recogida "false" y hora de devolución "true"
    And Pulso continuar para seleccionar quads
    And Selecciono el primer quad disponible
    And Confirmo la selección de quads
    And Confirmo la reserva en la pantalla de confirmación
    Then La reserva seleccionada muestra en detalle el nombre "Client 2" y el móvil "612345678"

  Scenario: Modificar una reserva válida desde detalle con mismas fechas, recogida por la mañana, devolución por la tarde y 1 quad seleccionado
    When Pulso sobre la primera reserva del listado para ver su detalle
    And Pulso el botón editar en el detalle de la reserva
    And Cambio el nombre a "Client 3" y el móvil a "612345678" en reserva
    And Introduzco fecha de recogida en 1 días y devolución en 3 días
    And Introduzco hora de recogida "false" y hora de devolución "true"
    And Pulso continuar para seleccionar quads
    And Selecciono el primer quad disponible
    And Confirmo la selección de quads
    And Confirmo la reserva en la pantalla de confirmación
    Then La reserva seleccionada muestra en detalle el nombre "Client 3" y el móvil "612345678"

  # nombre vacío al modificar
  Scenario: Intentar modificar una reserva dejando el nombre vacío
    When Pulso editar en la primera reserva del listado
    And Cambio el nombre a "" y el móvil a "612345678" en reserva
    And Introduzco fecha de recogida en 1 días y devolución en 3 días
    And Introduzco hora de recogida "true" y hora de devolución "false"
    Then Permanece en el formulario de reserva

  # móvil vacío al modificar
  Scenario: Intentar modificar una reserva dejando el móvil vacío
    When Pulso editar en la primera reserva del listado
    And Cambio el nombre a "Client 5" y el móvil a "" en reserva
    And Introduzco fecha de recogida en 1 días y devolución en 3 días
    And Introduzco hora de recogida "true" y hora de devolución "false"
    Then Permanece en el formulario de reserva

  # móvil inválido al modificar
  Scenario: Intentar modificar una reserva con móvil no numérico
    When Pulso editar en la primera reserva del listado
    And Cambio el nombre a "Client 6" y el móvil a "612ABC678" en reserva
    And Introduzco fecha de recogida en 1 días y devolución en 3 días
    And Introduzco hora de recogida "true" y hora de devolución "false"
    Then Permanece en el formulario de reserva

  # fecha de recogida pasada
  Scenario: Intentar modificar una reserva con fecha de recogida pasada
    When Pulso editar en la primera reserva del listado
    And Cambio el nombre a "Client 7" y el móvil a "612345678" en reserva
    And Introduzco fecha de recogida en -2 días y devolución en 1 días
    And Introduzco hora de recogida "true" y hora de devolución "false"
    Then Permanece en el formulario de reserva

  # fecha de devolución anterior a recogida
  Scenario: Intentar modificar una reserva con devolución anterior a recogida
    When Pulso editar en la primera reserva del listado
    And Cambio el nombre a "Client 8" y el móvil a "612345678" en reserva
    And Introduzco fecha de recogida en 3 días y devolución en 1 días
    And Introduzco hora de recogida "true" y hora de devolución "false"
    Then Permanece en el formulario de reserva

  # mismas fechas y recogida por la tarde
  Scenario: Intentar modificar una reserva con misma fecha y recogida por la tarde
    When Pulso editar en la primera reserva del listado
    And Cambio el nombre a "Client 9" y el móvil a "612345678" en reserva
    And Introduzco fecha de recogida en 1 días y devolución en 1 días
    And Introduzco hora de recogida "true" y hora de devolución "true"
    Then Permanece en el formulario de reserva

  # mismas fechas y devolución por la mañana
  Scenario: Intentar modificar una reserva con misma fecha y devolución por la mañana
    When Pulso editar en la primera reserva del listado
    And Cambio el nombre a "Client 10" y el móvil a "612345678" en reserva
    And Introduzco fecha de recogida en 1 días y devolución en 1 días
    And Introduzco hora de recogida "false" y hora de devolución "false"
    Then Permanece en el formulario de reserva

  # cancelar desde primer formulario
  Scenario: Cancelar la modificación desde el primer formulario
    When Pulso editar en la primera reserva del listado
    And Cambio el nombre a "No debe guardarse" y el móvil a "612345678" en reserva
    And Pulso cancelar en el formulario de reserva
    Then La reserva seleccionada no muestra en detalle el nombre "No debe guardarse"

  # cancelar desde último paso de modificación
  Scenario: Cancelar la modificación desde la pantalla de confirmación
    When Pulso editar en la primera reserva del listado
    And Cambio el nombre a "No debe guardarse 2" y el móvil a "612345678" en reserva
    And Introduzco fecha de recogida en 1 días y devolución en 3 días
    And Introduzco hora de recogida "true" y hora de devolución "false"
    And Pulso continuar para seleccionar quads
    And Selecciono el primer quad disponible
    And Confirmo la selección de quads
    And Cancelo la reserva en la pantalla de confirmación
    Then La reserva seleccionada no muestra en detalle el nombre "No debe guardarse"