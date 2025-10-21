package ru.bank.cards.entity;

/**
 * Статус перевода.
 */
public enum TransferStatus {

    COMPLETED("Завершена"),
    FAILED("Отменена");

    final String status;

    TransferStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Status{" +
                "status='" + status + '\'' +
                '}';
    }
}
