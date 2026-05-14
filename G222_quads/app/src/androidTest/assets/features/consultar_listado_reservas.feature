# Feature: Consultar listado de reservas
# Técnica: clases de equivalencia sobre filtros y criterios de ordenación
#
# Clases de equivalencia identificadas:
#
#   Filtro de estado:
#     1 – todas las reservas (sin filtro)
#     2 – solo reservas previstas (fechas futuras)
#     3 – solo reservas vigentes (en curso)
#     4 - solo reservas caducadas
#
#   Criterio de ordenación:
#     5 – orden por defecto
#     6 – ordenado por fecha de recogida
#     7 – ordenado por fecha de devolución
#     8 – ordenado por nombre de cliente
#     9 – ordenado por número de teléfono
#
#   Estado del listado:
#     9  (válida) – listado con una o más reservas
#     10 (válida) – listado vacío tras aplicar filtro
#
# Tabla de casos de prueba:
#   CP | Filtro      | Ordenación         | Reservas | Resultado esperado
#   1  | todas       | por defecto        | Sí       | listado visible con todas las reservas
#   2  | previstas   | por defecto        | Sí       | solo reservas previstas
#   3  | vigentes    | por defecto        | Sí       | solo reservas vigentes o vacío
#   4  | caducadas   | por defecto        | Sí       | solo reservas caducadas o vacío
#   5  | todas       | por fecha recogida | Sí       | listado ordenado por fecha recogida
#   6  | todas       | por fecha dev.     | Sí       | listado ordenado por fecha devolución
#   7  | todas       | por nombre         | Sí       | listado ordenado por nombre
#   8  | todas       | por teléfono       | Sí       | listado ordenado por teléfono

Feature: Consultar listado de reservas
  Como propietario de la empresa de alquiler
  Quiero poder ver el listado de reservas con distintos filtros y ordenaciones
  Para localizar rápidamente la información que necesito

  Background:
    Given Abro la aplicación de gestión de quads
    And Accedo a la sección de reservas

  # listado completo por defecto
  Scenario: Ver todas las reservas en orden por defecto
    Given Existen al menos dos reservas en el sistema
    Then El listado de reservas es visible con al menos un elemento

  # filtrar solo previstas
  Scenario: Filtrar el listado para mostrar solo reservas previstas
    Given Existen reservas previstas en el sistema
    When Selecciono el filtro "Reservas previstas" en el listado de reservas
    Then El listado muestra únicamente reservas con fecha de recogida futura

  # filtrar solo vigentes
  Scenario: Filtrar el listado para mostrar solo reservas vigentes
    When Selecciono el filtro "Reservas vigentes" en el listado de reservas
    Then El listado muestra únicamente reservas actualmente en curso o aparece vacío

  # filtrar solo caducadas
  Scenario: Filtrar el listado para mostrar solo reservas caducadas
    When Selecciono el filtro "Reservas caducadas" en el listado de reservas
    Then El listado muestra únicamente reservas con fecha de devolución pasada o aparece vacío

    # ordenar por fecha de recogida
  Scenario: Ordenar el listado de reservas por fecha de recogida
    Given Existen al menos dos reservas en el sistema
    When Selecciono ordenar reservas por fecha de recogida
    Then El listado de reservas aparece ordenado por fecha de recogida de forma ascendente

  # ordenar por fecha de devolución
  Scenario: Ordenar el listado de reservas por fecha de devolución
    Given Existen al menos dos reservas en el sistema
    When Selecciono ordenar reservas por fecha de devolución
    Then El listado de reservas aparece ordenado por fecha de devolución de forma ascendente

  # ordenar por nombre
  Scenario: Ordenar el listado de reservas por nombre de cliente
    Given Existen al menos dos reservas en el sistema
    When Selecciono ordenar reservas por nombre de cliente
    Then El listado de reservas aparece ordenado alfabéticamente por nombre de cliente

  # ordenar por teléfono
  Scenario: Ordenar el listado de reservas por número de teléfono
    Given Existen al menos dos reservas en el sistema
    When Selecciono ordenar reservas por número de teléfono
    Then El listado de reservas aparece ordenado por número de teléfono de forma ascendente
