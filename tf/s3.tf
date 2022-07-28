resource "aws_s3_bucket" "accountability_receipt_photo_bucket" {
  bucket = "accountability-receipt-photo"

  tags = {
    Name        = "accountability-receipt-photo"
  }
}

resource "aws_s3_bucket_acl" "accountability_receipt_photo_bucket_acl" {
  bucket = aws_s3_bucket.accountability_receipt_photo_bucket.id
  acl    = "private"
}

resource "aws_s3_bucket_versioning" "accountability_receipt_photo_bucket_versioning" {
  bucket = aws_s3_bucket.accountability_receipt_photo_bucket.id
  versioning_configuration {
    status = "Enabled"
  }
}