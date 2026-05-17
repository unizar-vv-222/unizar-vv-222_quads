# Feature: Crear quad
# Técnica: clases de equivalencia sobre los campos del formulario de creación
#
# Clases de equivalencia identificadas:
#
#   Campo matrícula:
#     1 (válida)   – matrícula con formato correcto (ej. "1234ABC")
#     2 (válida)   - matrícula no existente en el sistema
#     3 (inválida) – matrícula con formato incorrecto
#     4 (inválida) – matrícula ya existente en el sistema
#
#   Campo tipo:
#     5 (válida)   – tipo True (biplaza)
#     6 (válida)   - tipo False (monoplaza)
#
#   Campo precio:
#     7 (válida)   – precio positivo (ej. 50.0)
#     8 (inválida) – precio cero o negativo
#
#   Campo descripción:
#     9 (válida)   – descripción no vacía
#     10 (inválida) – descripción vacía
#
#   Confirmación del diálogo:
#     11 (válida)   – usuario confirma la creación (guardar)
#     12 (válida)   – usuario cancela la creación
#
# Tabla de casos de prueba:
#   CP | Matrícula  | Tipo  | Precio | Descripción | Acción     | Resultado esperado            | Anotaciones                     | Clases cubiertas
#   1  | "1234ABC"  | True  | 50.0   | "Quad X"    | guardar    | quad creado                   | -                               | 1, 2, 5, 7, 9 y 11
#   2  | "4321CBA"  | False | 50.0   | "Quad X"    | guardar    | quad creado                   | -                               | 1, 2, 6, 7, 9 y 11
#   3  | ""         | False | 50.0   | "Quad X"    | guardar    | error / botón guardar         | -                               | 3
#      |            |       |        |             |            | deshabilitado                 |                                 |
#   4  | "1234ABC"  | False | 50.0   | "Quad Y"    | guardar    | error matrícula  duplicada    | Ya existe un quad con matrícula | 4
#      |            |       |        |             |            | / botón guardar deshabilitado | "1234ABC" en el sistema.        |
#   5  | "5678DEF"  | False | -10.0  | "Quad X"    | guardar    | error / botón guardar         | -                               | 8
#      |            |       |        |             |            | deshabilitado                 |                                 |
#   6  | "5678DEF"  | False | 50.0   | ""          | guardar    | error / botón guardar         | -                               | 10
#      |            |       |        |             |            | deshabilitado                 |                                 |
#   7  | "1235ABC"  | True  | 50.0   | "Quad X"    | cancelar   | quad sin cambios en listado   | -                               | 1, 2, 5, 7, 9 y 12

Feature: Crear quad
  Como propietario de la empresa de alquiler
  Quiero poder crear nuevos quads en el sistema
  Para tener el catálogo actualizado

  Background:
    Given Abro la aplicación de gestión de quads
    And Accedo a la sección de quads

  # casos válidos
  Scenario: Crear un nuevo quad biplaza con datos correctos
    When Pulso el botón de nuevo quad
    And Introduzco la matrícula "1234ABC", el precio "50.0", el tipo "true" y la descripcion "Quad X"
    And Pulso guardar en el formulario de quad
    Then El quad con matrícula "1234ABC" aparece en el listado de quads

  Scenario: Crear un nuevo quad monoplaza con datos correctos
    When Pulso el botón de nuevo quad
    And Introduzco la matrícula "4321CBA", el precio "50.0", el tipo "false" y la descripcion "Quad X"
    And Pulso guardar en el formulario de quad
    Then El quad con matrícula "4321CBA" aparece en el listado de quads

  # matrícula con formato incorrecto
  Scenario: Intentar crear un quad con matrícula con formato incorrecto
    When Pulso el botón de nuevo quad
    And Introduzco la matrícula "", el precio "50.0", el tipo "false" y la descripcion "Quad X"
    And Pulso guardar en el formulario de quad
    Then Permanece en el formulario de quad

  # matrícula duplicada
  Scenario: Intentar crear un quad con matrícula ya existente
    Given Ya existe un quad con matrícula "1234ABC" en el sistema
    When Pulso el botón de nuevo quad
    And Introduzco la matrícula "1234ABC", el precio "50.0", el tipo "false" y la descripcion "Quad Y"
    And Pulso guardar en el formulario de quad
    Then Permanece en el formulario de quad

  # precio negativo
  Scenario: Intentar crear un quad con precio negativo
    When Pulso el botón de nuevo quad
    And Introduzco la matrícula "5678DEF", el precio "-10.0", el tipo "false" y la descripcion "Quad X"
    And Pulso guardar en el formulario de quad
    Then Permanece en el formulario de quad

  # descripción vacía
  Scenario: Intentar crear un quad sin descripción
    When Pulso el botón de nuevo quad
    And Introduzco la matrícula "5678DEF", el precio "50.0", el tipo "false" y la descripcion ""
    And Pulso guardar en el formulario de quad
    Then Permanece en el formulario de quad

  # cancelar la edición
  Scenario: Cancelar la creación de un quad
    When Pulso el botón de nuevo quad
    And Introduzco la matrícula "1235ABC", el precio "50.0", el tipo "true" y la descripcion "Quad X"
    And Pulso cancelar en el formulario de quad
    Then El quad con matrícula "1235ABC" no aparece en el listado de quads
