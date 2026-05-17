# Feature: Consultar listado de quads
# Técnica: clases de equivalencia sobre los criterios de ordenación
#
# Clases de equivalencia identificadas:
#
#   Criterio de ordenación:
#     1 – ordenado por defecto (por matrícula)
#     2 – ordenado por matrícula
#     3 – ordenado por tipo
#     4 – ordenado por precio
#
#   Estado del listado:
#     5 (válida)   – listado con uno o más quads
#     6 (válida)   – listado vacío (sin quads creados)
#
# Tabla de casos de prueba:
#   CP | Ordenación        | Quads existentes | Resultado esperado
#   1  | por defecto       | Sí               | listado visible
#   2  | por matrícula     | Sí               | listado ordenado por matrícula
#   3  | por tipo          | Sí               | listado ordenado por tipo
#   4  | por precio        | Sí               | listado ordenado por precio
#   5  | por defecto       | No               | listado vacío, sin elementos

Feature: Consultar listado de quads
  Como propietario de la empresa de alquiler
  Quiero poder ver el listado de quads con distintos criterios de ordenación
  Para localizar rápidamente los quads que necesito

  Background:
    Given Abro la aplicación de gestión de quads
    And Accedo a la sección de quads

  # listado por defecto con quads
  Scenario: Ver el listado de quads en orden por defecto
    Given Existen al menos dos quads en el sistema
    Then El listado de quads es visible con al menos un elemento

  # ordenar por matrícula
  Scenario: Ordenar el listado de quads por matrícula
    Given Existen al menos dos quads en el sistema
    When Selecciono ordenar quads por matrícula
    Then El listado de quads aparece ordenado por matrícula de forma ascendente

  # ordenar por tipo
  Scenario: Ordenar el listado de quads por tipo
    Given Existen al menos dos quads de distintos tipos en el sistema
    When Selecciono ordenar quads por tipo
    Then El listado de quads aparece ordenado por tipo

  # ordenar por precio
  Scenario: Ordenar el listado de quads por precio
    Given Existen al menos dos quads con distintos precios en el sistema
    When Selecciono ordenar quads por precio
    Then El listado de quads aparece ordenado por precio de forma ascendente

  # listado vacío
  Scenario: Ver el listado de quads cuando no hay ninguno creado
    Given No existe ningún quad en el sistema
    Then El listado de quads está vacío
