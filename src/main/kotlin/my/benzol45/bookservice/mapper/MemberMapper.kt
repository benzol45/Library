package my.benzol45.bookservice.mapper

import my.benzol45.bookservice.domain.Member
import my.benzol45.bookservice.model.response.MemberDto
import my.benzol45.bookservice.model.request.RegisterMemberDto
import org.mapstruct.Mapper
import org.mapstruct.Mapping

@Mapper(componentModel = "spring")
interface MemberMapper {
    fun memberToMemberDTO(member: Member): MemberDto

    @Mapping(target = "block", constant = "false")
    @Mapping(target = "checkedOutBooks", expression = "java(new java.util.ArrayList<>())")
    fun registerMemberDTOToMember(registerMemberDTO: RegisterMemberDto): Member
}