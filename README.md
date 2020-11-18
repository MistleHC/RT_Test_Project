# Test appliation for RinfTech Internship


![Service-diagram](Service-diagram.png)



Project initialization process: 
1. Create new GCP project at Google Cloud Console. Create service account and download the credentials if you want to change/test project localy. 
2. Create avro files bucket in Google Cloud Storage at this project.
3. Enable API's for Pub/Sub, Cloud Storage, Cloud Run, BigQuery Tables.
4. Create two tables at BigQuery accordingly to: 
  Table name: client_full
  Fields: id integer, name string, phone string, address string, verified boolean, bill float;
  Table name: client_required
  Fields: id integer, name string;
5. Create Pub/Sub topic and push subscription configured for endpoint which specified at "BucketController".
6. Compile and deploy project to Google Cloud Run. 

To generate new avro file and appload it to the bucket it's can be used the following api or you can do it manualy. 
- #POST on /api/avro/generate: 
{
long "id" Required, 
string "name" Required, 
string "phone", 
string "address", 
boolean "verified", 
float "bill"
} 
