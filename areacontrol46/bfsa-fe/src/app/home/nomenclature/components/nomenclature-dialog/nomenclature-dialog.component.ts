import {
  AfterViewInit,
  Component,
  ElementRef,
  Inject,
  ViewChild,
} from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { NomenclatureInterface } from '../../interfaces/nomenclature-interface';

@Component({
  selector: 'app-nomenclature-dialog',
  templateUrl: './nomenclature-dialog.component.html',
  styleUrls: ['./nomenclature-dialog.component.scss'],
})
export class NomenclatureDialogComponent implements AfterViewInit {
  @ViewChild('dataContainer') dataContainer: ElementRef;

  public svgPath: any;
  public file: any;
  dialogForm = this.fb.group({
    name: ['', Validators.required],
    description: '',
    enabled: true,
    code: '',
    symbol: null,
  });

  public get isFileUploaded() {
    return this.dialogForm.get('symbol')?.value;
  }

  public get doesFileExist() {
    return this.file
  }

  constructor(
    private fb: FormBuilder,
    public dialogRef: MatDialogRef<NomenclatureInterface>,
    @Inject(MAT_DIALOG_DATA)
    public data: { isAdd: boolean; nomenclature: NomenclatureInterface }
  ) {
    dialogRef.disableClose = true;
  }

  ngOnInit(): void {
    if (this.data.nomenclature) {
      this.dialogForm.patchValue({
        name: this.data.nomenclature.name,
        description: this.data.nomenclature.description,
        enabled: this.data.nomenclature.enabled,
        code: this.data.nomenclature.code,
        symbol: this.data.nomenclature.symbol,
      });
    }
  }

  ngAfterViewInit() {
    if (this.data?.nomenclature?.symbol) {
      this.file = true;
      this.dataContainer.nativeElement.innerHTML =
        this.data.nomenclature.symbol;
      let svg = this.dataContainer.nativeElement.querySelector('svg');
      svg.setAttribute('width', '100px');
      svg.setAttribute('height', '100px');
    }
  }

  onSubmit() {
    this.dialogRef.close(this.dialogForm.value);
  }

  removeFile() {
    this.dialogForm.get('symbol')?.setValue(null);
    this.dataContainer.nativeElement.innerHTML = null;
    this.dialogForm.markAsDirty();
    this.file = null;
  }

  uploadedFile(event: any) {
    this.file = event.target.files[0];
    this.dialogForm.markAsDirty();
    //to enable the add button
    let fileReader = new FileReader();
    fileReader.readAsBinaryString(this.file);
    fileReader.onload = (e) => {
      let symbol = fileReader.result as any;
      this.dataContainer.nativeElement.innerHTML = symbol;
      let svg = this.dataContainer.nativeElement.querySelector('svg');
      svg.setAttribute('width', '100px');
      svg.setAttribute('height', '100px');
      this.dialogForm.get('symbol')?.setValue(symbol);
    };
  }
}
