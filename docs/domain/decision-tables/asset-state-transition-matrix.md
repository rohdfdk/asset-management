### 資産貸出状態遷移マトリクス（Asset State Transition Matrix）

| 現在の状態（From） \ イベント（Event） | 貸出<br>(Rent) | 返却<br>(Return) | メンテ開始 / 故障<br>(Maintenance / Fail) | メンテ完了<br>(Repair Complete) | 廃棄 / 修理不能<br>(Retire) |
| :--- | :---: | :---: | :---: | :---: | :---: |
| **AVAILABLE**<br>（利用可能） | **LOANED** | x | **MAINTENANCE** | - | **RETIRED** |
| **LOANED**<br>（貸出中） | - | **AVAILABLE** | **MAINTENANCE** | x | x |
| **MAINTENANCE**<br>（メンテ中） | x | x | - | **AVAILABLE** | **RETIRED** |
| **RETIRED**<br>（廃棄済） | x | x | x | x | - |

#### 💡 記号の見方（凡例）
* **`[x]` (エラー)** ： 実行不可（画面にエラーを表示し、処理をブロックする）
* **`[-]` (無視/不変)** ： 処理スキップ（状態は変わらず、そのまま操作をスルーする）