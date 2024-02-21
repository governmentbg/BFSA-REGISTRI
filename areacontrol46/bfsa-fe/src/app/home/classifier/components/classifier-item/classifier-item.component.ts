import { Component, OnInit } from '@angular/core';
import { Location } from '@angular/common';
import { ClassifierInterface } from '../../interfaces/classifier-interface';
import { ActivatedRoute, Params } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatDialog } from '@angular/material/dialog';
import { ClassifierService } from '../../services/classifier.service';
import { ClassifierDialogComponent } from '../classifier-dialog/classifier-dialog.component';

@Component({
  selector: 'app-classifier-item',
  templateUrl: './classifier-item.component.html',
  styleUrls: ['./classifier-item.component.scss'],
})
export class ClassifierItemComponent implements OnInit {
  classifier: ClassifierInterface;

  constructor(
    private classifierService: ClassifierService,
    private activatedRoute: ActivatedRoute,
    private location: Location,
    private _snackBar: MatSnackBar,
    public dialog: MatDialog
  ) {}

  ngOnInit(): void {
    this.initialize();
  }

  initialize() {
    this.activatedRoute.params.subscribe((params: Params) => {
      const id = params['id'];
      this.classifierService
        .getClassifier(id)
        .subscribe((classifier: ClassifierInterface) => {
          this.classifier = classifier;
        });
    });
  }

  editClassifier(classifier: ClassifierInterface) {
    const dialogRef = this.dialog.open(ClassifierDialogComponent, {
      width: '400px',
      data: { isAdd: false, classifier },
    });
    dialogRef.afterClosed().subscribe((classifier: ClassifierInterface) => {
      if (classifier) {
        console.log(classifier);
        this.classifierService
          .updateClassifier(classifier.code, classifier)
          .subscribe({
            next: () => {
              this.initialize();
              this._snackBar.open('Classifier updated successfully', '', {
                duration: 1000,
              });
            },
            error: (err) => {
              this._snackBar.open(
                `Error while updating the classifier: ${err.message}`,
                'Close'
              );
            },
          });
      }
    });
  }

  addSubClassifier() {
    const dialogRef = this.dialog.open(ClassifierDialogComponent, {
      width: '400px',
      data: { isAdd: true },
    });
    dialogRef.afterClosed().subscribe((classifier: ClassifierInterface) => {
      if (classifier) {
        console.log(classifier);
        this.classifierService
          .addSubClassifier(this.classifier.code, classifier)
          .subscribe({
            next: () => {
              this._snackBar.open('Subclassifier added successfully', '', {
                duration: 1000,
              });
              this.initialize();
            },
            error: (err) => {
              this._snackBar.open(
                `Error while adding the subclassifier type: ${err.message}`,
                'Close'
              );
            },
          });
      }
    });
  }

  back(): void {
    this.location.back();
  }
}
