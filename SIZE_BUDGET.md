# Size budget

180FlipCharge is a micro-utility. Binary size is a product requirement.

- Release APK hard gate: **<= 8 MiB**
- Release AAB hard gate: **<= 10 MiB**
- Desired clean installed footprint: **<= 25 MiB**, measured separately on a real device

Release uses R8 and resource shrinking. Do not raise the limits to silence a regression: identify the dependency/resource responsible first.
