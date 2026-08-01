package models

type Deployment struct {
	Spec     Spec     `json:"spec"`
	Metadata Metadata `json:"metadata"`
}
