package models

type Spec struct {
	Replicas int32    `json:"replicas"`
	Selector Selector `json:"selector"`
	Template Template `json:"template"`
}
