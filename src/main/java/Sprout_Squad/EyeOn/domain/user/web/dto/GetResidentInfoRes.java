package Sprout_Squad.EyeOn.domain.user.web.dto;

public record GetResidentInfoRes (
        String name,
        String residentNumber,
        String residentDate,
        String address
){
    public GetResidentInfoRes of(String name, String residentNumber, String residentDate, String address){
        return new GetResidentInfoRes(name, residentNumber, residentDate, address);
    }
}
