# OA Features

| Feature List | Feature Description | Completed |
|:-------------|:--------------------|:---------:|
| **SIMPLE Designer** | DingTalk/Feishu-style designer; supports drag-and-drop form process building; complete approval process configuration in 10 minutes | ✅ |
| **BPMN Designer** | Developed based on BPMN standards; adapts to complex business scenarios; meets multi-level approval and process automation requirements | ✅ |
| **Countersign** | Set multiple people at the same approval node (e.g., A, B, C); all three receive tasks simultaneously; requires all approvals before proceeding to next node | ✅ |
| **Or-Sign** | Set multiple people at the same approval node; any one person processing allows moving to the next node | ✅ |
| **Sequential Approval** | (Sequential Countersign) Set multiple people at the same approval node (e.g., A, B, C); three receive tasks in sequence (A approves first, then B after A submits); requires all approvals before proceeding to next node | ✅ |
| **CC** | Notify approval results to CC persons; deduplicates by default; does not CC the same person repeatedly | ✅ |
| **Reject** | (Return) Reset approval to send to a specific node for re-approval. Can reject to initiator, previous node, or any node | ✅ |
| **Transfer** | A transfers to B for approval; after B approves, proceed to next node | ✅ |
| **Delegate** | A delegates to B for approval; after B approves, returns to A; after A continues approving, proceed to next node | ✅ |
| **Add Approver** | Allows current approver to add additional approvers to the current node as needed; supports adding before or after | ✅ |
| **Remove Approver** | (Cancel Add Approver) Remove approvers before the current approver operates | ✅ |
| **Withdraw** | (Cancel Process) Process initiator can withdraw the process | ✅ |
| **Terminate** | System administrator can terminate process instance at any node | ✅ |
| **Form Permissions** | Supports drag-and-drop form configuration; each approval node can configure read-only, edit, and hidden permissions | ✅ |
| **Timeout Approval** | Configure timeout approval duration; automatically triggers approval pass, reject, or return actions after timeout | ✅ |
| **Auto Reminder** | Configure reminder time; automatically triggers SMS, email, or in-site message notifications; supports custom reminder frequency | ✅ |
| **Parent-Child Process** | Main process sets subprocess node; subprocess node automatically triggers subprocess. After subprocess completes, main process continues execution; supports synchronous and asynchronous subprocesses | ✅ |
| **Conditional Branch** | (Exclusive Branch) Used for decision-making in processes; selects one branch to execute based on conditions | ✅ |
| **Parallel Branch** | Allows splitting process into multiple branches; no conditional judgment; all branches execute | ✅ |
| **Inclusive Branch** | (Combination of Conditional Branch + Parallel Branch) Allows selecting multiple branches to execute based on conditions; if no branch meets conditions, can select default branch | ✅ |
| **Route Branch** | Select one branch to execute based on conditions (redirect to specified configured node), or select default branch to execute (continue downward) | ✅ |
| **Trigger Node** | When reaching this node, triggers HTTP request, HTTP callback, data update, data deletion, etc. | ✅ |
| **Delay Node** | When reaching this node, approval waits for a period before executing; supports fixed duration, fixed date, etc. | ✅ |
| **Extended Settings** | Process pre/post notifications, node (task) pre/post notifications, process reports, automatic approval deduplication, custom process number/title/summary, process reports, etc. | ✅ |
