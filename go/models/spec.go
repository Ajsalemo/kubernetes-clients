package models

type Spec struct {
	Replicas int      `json:"replicas"`
	Selector Selector `json:"selector"`
	Template Template `json:"template"`
}
