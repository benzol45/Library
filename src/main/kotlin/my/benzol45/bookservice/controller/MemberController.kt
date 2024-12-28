package my.benzol45.bookservice.controller

import lombok.RequiredArgsConstructor
import my.benzol45.bookservice.model.request.RegisterMemberDto
import my.benzol45.bookservice.model.response.MemberDto
import my.benzol45.bookservice.service.MemberService
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
class MemberController (
    private val memberService: MemberService
){
    @GetMapping
    fun getAllMembers(
        @RequestParam(name = "page", required = false, defaultValue = "0") page: Int,
        @RequestParam(name = "size", required = false, defaultValue = "20") size: Int
    ): Page<MemberDto> =
        memberService.getAllMembers(PageRequest.of(page,size))

    @GetMapping("/filter")
    fun getFilteredMembers(
        @RequestParam(name = "surname", required = false) surname: String?,
        @RequestParam(name = "email", required = false) email: String?,
        @RequestParam(name = "phone", required = false) phone: String?
    ): List<MemberDto> =
        memberService.getFilteredMembers(surname, email, phone)

    @GetMapping("/{id}")
    fun getMember(@PathVariable("id") id: Long): ResponseEntity<MemberDto> =
        memberService.getMember(id)
            ?.let { ResponseEntity.ok(it) }
            ?: ResponseEntity.notFound().build()

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createMember(@RequestBody registerMemberDto: RegisterMemberDto): MemberDto =
        memberService.createMember(registerMemberDto)

    @PatchMapping("/{id}/block")
    fun blockMember(@PathVariable("id") id: Long): ResponseEntity<Unit> =
        memberService.processBlockMember(id, true)
            ?.let { ResponseEntity.ok().build() }
            ?: ResponseEntity.notFound().build()

    @PatchMapping("/{id}/unblock")
    fun unblockMember(@PathVariable("id") id: Long): ResponseEntity<Unit> =
        memberService.processBlockMember(id, false)
            ?.let { ResponseEntity.ok().build() }
            ?: ResponseEntity.notFound().build()

}