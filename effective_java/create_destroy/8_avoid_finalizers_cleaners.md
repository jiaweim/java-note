# 避免使用 finalizer 和 cleaner

Finalizer 不可预测，通常是危险且没有必要。使用 Finalizer 会导致不稳定行为、性能差以及可移植性问题。Finalizer 有以协议用途，后面会介绍，但建议避免使用。从 Java 9 开始，Finalizer 被弃用，不过依然在 Java 类库中使用。Java 9 用 Cleaner 替代了 Finalizer。Cleaner 比 Finalizer 更安全，但依然不可预测、速度慢，且没有必要。


