# BLOQUE 1 – Pruebas de aceptación funcionales (Scenario Testing)
# Técnica: scenario testing (flujos reales de usuario sobre la UI)

Feature: Gestión de reservas de quads
  Como propietario de una empresa de alquiler de quads
  Quiero poder crear, consultar y eliminar reservas
  Para gestionar el negocio de forma eficiente

  # ──────────────────────────────────────────────
  # Escenario 1: Crear una reserva completa
  # Flujo: abrir app → sección reservas → nueva reserva → rellenar → seleccionar quad → confirmar
  # ──────────────────────────────────────────────
  Scenario: Crear una reserva nueva con un quad seleccionado
    Given Abro la aplicación de gestión de quads
    And Accedo a la sección de reservas
    When Pulso el botón de nueva reserva
    And Relleno el formulario con nombre "Client 1" y teléfono "612345678"
    And Selecciono una fecha de recogida futura
    And Selecciono una fecha de devolución posterior a la recogida
    And Pulso continuar para seleccionar quads
    And Selecciono el primer quad disponible
    And Confirmo la selección de quads
    And Confirmo la reserva
    Then La reserva aparece en el listado de reservas

  # ──────────────────────────────────────────────
  # Escenario 2: Cancelar una reserva en el formulario
  # Flujo: abrir formulario → cancelar → volver al listado sin crear nada nuevo
  # ──────────────────────────────────────────────
  Scenario: Cancelar la creación de una reserva
    Given Abro la aplicación de gestión de quads
    And Accedo a la sección de reservas
    When Pulso el botón de nueva reserva
    And Relleno el formulario con nombre "Client 1" y teléfono "612345678"
    And Pulso el botón cancelar en el formulario de reserva
    Then Vuelvo al listado de reservas sin crear ninguna nueva

  # ──────────────────────────────────────────────
  # Escenario 3: Ver el detalle de una reserva existente
  # Flujo: listado → pulsar reserva → ver detalle
  # ──────────────────────────────────────────────
  Scenario: Consultar el detalle de una reserva existente
    Given Abro la aplicación de gestión de quads
    And Accedo a la sección de reservas
    When Pulso sobre la primera reserva del listado
    Then Se muestra la pantalla de detalle de la reserva

  # ──────────────────────────────────────────────
  # Escenario 4: Eliminar una reserva desde su detalle
  # Flujo: listado → detalle → eliminar → confirmar → volver al listado
  # ──────────────────────────────────────────────
  Scenario: Eliminar una reserva desde su pantalla de detalle
    Given Abro la aplicación de gestión de quads
    And Accedo a la sección de reservas
    When Pulso sobre la primera reserva del listado
    And Pulso el botón eliminar en el detalle
    And Confirmo la eliminación
    Then Vuelvo al listado de reservas
