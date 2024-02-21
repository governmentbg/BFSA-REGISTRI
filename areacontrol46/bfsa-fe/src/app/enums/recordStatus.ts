export enum recordStatus {
  ENTERED = 'Входящ',
  ACTIVE = 'Активен',
  PAYMENT_CONFIRMATION = 'Потвърждаване на плащане',
  PAYMENT_CONFIRMED = 'Потвърдено плащане',
  PAYMENT_REJECTED = 'Отказано плащане',
  PROCESSING = 'В обработка',
  FOR_CORRECTION = 'За корекция',
  INSPECTION = 'Проверка на обекта',
  INSPECTION_COMPLETED = 'Завършена проверка',
  FINAL_APPROVED = 'Одобрен',
  FINAL_REJECTED = 'Отказан',
}
