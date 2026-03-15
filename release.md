# Release history

## v0.3.0, 2026-03-15
- Add support for comparing GStrings in assertEquals. This allows for assertions that GStrings are equal to Strings, which is useful for cases where string interpolation is used and the resulting strings need to be compared.
- Change to CompileStatic for assertEquals to improve performance and type safety. This ensures that the method is compiled with static type checking, which can catch potential issues at compile time and improve the overall performance of the assertions.
- Fix assertEquals(Number, BigDecimal, BigDecimal) so one-sided null values fail with an assertion instead of throwing, and so numeric delta
    comparisons work correctly under @CompileStatic.

## v0.2.0, 2026-02-27
- Add support for delta handling in assertEquals for BigDecimal comparisons. This allows for assertions that two BigDecimals are equal within a specified tolerance, which is useful for cases where exact equality is not expected due to rounding or precision issues.

## v0.1.0, 2026-02-21
- Initial Release.