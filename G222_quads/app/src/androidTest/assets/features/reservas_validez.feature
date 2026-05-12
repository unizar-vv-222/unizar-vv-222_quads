# Fichero: app/src/androidTest/assets/features/reservas_validez.feature
#
# BLOQUE 2 – Pruebas de aceptación funcionales (Caja negra)
# Técnica: particiones de equivalencia
# Reglas probadas:
#   R1 – Validez de las reservas (fechas, campos obligatorios)
#   R2 – El precio total de la reserva no cambia si se modifica el precio del quad
# Herramienta: Cucumber + Espresso

Feature: Validez y precio de las reservas de quads
  Como propietario de la empresa
  Quiero que el sistema sólo acepte reservas con datos correctos
  Y que el precio pactado no varíe aunque cambie el precio del quad

  # ──────────────────────────────────────────────
  # R1 – Validez de reservas: particiones de equivalencia
  #
  # Particiones:
  #   PE1 (válida)   – todos los campos correctos, fechas futuras, recogida < devolución
  #   PE2 (inválida) – nombre de cliente vacío
  #   PE3 (inválida) – teléfono vacío
  #   PE4 (inválida) – fecha devolución anterior o igual a recogida  (no llega a select_quads)
  # ──────────────────────────────────────────────

  Scenario Outline: Validar el formulario de reserva con distintas combinaciones de datos
    Given Abro la aplicación de gestión de quads
    And Accedo a la sección de reservas
    When Pulso el botón de nueva reserva
    And Introduzco nombre "<nombre>" y teléfono "<telefono>"
    And Introduzco fechas con desplazamiento de recogida "<diasRecogida>" y devolución "<diasDevolucion>"
    Then El botón continuar "<resultado>"

    Examples:
      | nombre      | telefono  | diasRecogida | diasDevolucion | resultado  |
      | Ana García  | 612345678 | 1            | 3              | funciona   |
      |             | 612345678 | 1            | 3              | no funciona|
      | Ana García  |           | 1            | 3              | no funciona|
      | Ana García  | 612345678 | 3            | 1              | no funciona|

  # ──────────────────────────────────────────────
  # R2 – Precio de la reserva se mantiene aunque cambie el precio del quad
  #
  # Particiones:
  #   PE5 (válida)  – se crea una reserva y el precio del quad sube: precio reserva no cambia
  #   PE6 (válida)  – se crea una reserva y el precio del quad baja: precio reserva no cambia
  # ──────────────────────────────────────────────

  Scenario: El precio de la reserva no cambia cuando sube el precio del quad
    Given Abro la aplicación de gestión de quads
    And Existe un quad con matrícula "TEST1AA" y precio "50.0" euros por día
    And Existe una reserva para ese quad con precio total calculado
    When Modifico el precio del quad "TEST1AA" a "200.0" euros por día
    Then El precio total de la reserva sigue siendo el mismo que al crearla

  Scenario: El precio de la reserva no cambia cuando baja el precio del quad
    Given Abro la aplicación de gestión de quads
    And Existe un quad con matrícula "TEST2BB" y precio "100.0" euros por día
    And Existe una reserva para ese quad con precio total calculado
    When Modifico el precio del quad "TEST2BB" a "10.0" euros por día
    Then El precio total de la reserva sigue siendo el mismo que al crearla
