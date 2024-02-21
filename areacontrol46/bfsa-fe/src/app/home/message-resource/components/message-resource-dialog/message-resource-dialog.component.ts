import { Component, Inject, OnInit } from '@angular/core';
import { FormArray, UntypedFormBuilder, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MessageResourceInterface } from '../../interfaces/message-resource-interface';

@Component({
  selector: 'app-message-resource-dialog',
  templateUrl: './message-resource-dialog.component.html',
  styleUrls: ['./message-resource-dialog.component.scss'],
})
export class MessageResourceDialogComponent implements OnInit {
  dialogForm = this.fb.group({
    code: '',
    messages: this.fb.array([]),
  });

  constructor(
    private fb: UntypedFormBuilder,
    public dialogRef: MatDialogRef<MessageResourceDialogComponent>,
    @Inject(MAT_DIALOG_DATA)
    public data: {
      messages: MessageResourceInterface[];
    }
  ) {
    dialogRef.disableClose = true;
  }

  ngOnInit(): void {
    if (this.data.messages) {
      console.log(this.data.messages);

      this.dialogForm.patchValue({
        code: this.data.messages[0].code,
      });
      this.data.messages.forEach((message) => {
        const control = this.fb.group({
          languageId: message.languageId,
          code: message.code,
          message: [message.message, [Validators.required]],
        });
        (this.dialogForm.controls['messages'] as FormArray).push(control);
      });
    }
  }

  onSubmit() {
    console.log(this.dialogForm.controls['messages'].value);

    const messages = this.dialogForm.controls['messages'].value;
    this.dialogRef.close(messages);
  }
}
